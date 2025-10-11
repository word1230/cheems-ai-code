package com.cheems.cheemsaicode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import com.cheems.cheemsaicode.ai.AiCodeGenTypeRoutingService;
import com.cheems.cheemsaicode.ai.AiCodeGeneratorServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cheems.cheemsaicode.ai.core.AICodeGeneratorFacade;
import com.cheems.cheemsaicode.ai.core.builder.VueProjectBuilder;
import com.cheems.cheemsaicode.ai.core.handler.StreamHandlerExecutor;
import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;
import com.cheems.cheemsaicode.constant.AppConstant;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.model.dto.app.AppAddRequest;
import com.cheems.cheemsaicode.model.dto.app.AppQueryRequest;
import com.cheems.cheemsaicode.model.entity.User;
import com.cheems.cheemsaicode.model.enums.MessageTypeEnum;
import com.cheems.cheemsaicode.model.vo.AppVO;
import com.cheems.cheemsaicode.service.ChatHistoryService;
import com.cheems.cheemsaicode.service.ScreenshotService;
import com.cheems.cheemsaicode.utils.ResultUtils;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.cheems.cheemsaicode.model.entity.App;
import com.cheems.cheemsaicode.mapper.AppMapper;
import com.cheems.cheemsaicode.service.AppService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 应用 服务层实现。
 *
 * @author cheems
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    private final AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    private final AICodeGeneratorFacade aICodeGeneratorFacade;
    private final ChatHistoryService chatHistoryService;
    private final StreamHandlerExecutor streamHandlerExecutor;
    private final VueProjectBuilder vueProjectBuilder;
    private final ScreenshotService screenshotService;
    private final AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService;

    @Override
    public void validApp(App app, boolean add) {
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);
        String appName = app.getAppName();
        String initPrompt = app.getInitPrompt();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StrUtil.isBlank(appName), ErrorCode.PARAMS_ERROR, "应用名称不能为空");
            ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "应用初始化 prompt 不能为空");
        }
        // 修改时，有参数则校验
        if (StrUtil.isNotBlank(appName)) {
            ThrowUtils.throwIf(appName.length() > 80, ErrorCode.PARAMS_ERROR, "应用名称过长");
        }
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtils.copyProperties(app, appVO);
        return appVO;
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return null;
        }
        return appList.stream()
                .map(this::getAppVO)
                .toList();
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            return null;
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();

        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public Flux<String> chatToGenCode(String userMessage, Long appId, User loginUser) {
        ThrowUtils.throwIf(appId == null && appId <= 0, ErrorCode.PARAMS_ERROR, "应用Id不能为空");
        ThrowUtils.throwIf(userMessage == null, ErrorCode.PARAMS_ERROR, "用户信息不能为空");

        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 验证十分有权限，只有用户本人可以生成代码
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限");

        // 保存用户消息到对话历史
        chatHistoryService.saveChatHistory(appId, loginUser.getId(), userMessage, MessageTypeEnum.USER);

        // 获取生成代码的类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum genType = CodeGenTypeEnum.getEnumByValue(codeGenType);
        ThrowUtils.throwIf(genType == null, ErrorCode.PARAMS_ERROR, "无效的生成代码类型");

        Flux<String> codestream = aICodeGeneratorFacade.generateAndSaveCodeStream(userMessage, genType, appId);
        ;

        // 执行流处理器
        return streamHandlerExecutor.doExecute(codestream, chatHistoryService, appId, loginUser, genType);

    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 校验参数
        ThrowUtils.throwIf(appId == null && appId <= 0, ErrorCode.PARAMS_ERROR, "应用Id不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        // 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 验证是否为本人，只有本人可以部署
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限");

        // 检查是否已有deploykey ， 没有责生成
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }

        // 获取代码类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;

        // 检查目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }

        // vue项目处理
        // 如果是vue 项目， 则先执行依赖安装，再执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {

            // 构建Vue项目
            boolean buildSuccess = vueProjectBuilder.buildVueProject(sourceDirPath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "构建失败");

            // 判断dist是否存在
            File distDir = new File(sourceDirPath, "dist");
            ThrowUtils.throwIf(!distDir.exists() || !distDir.isDirectory(), ErrorCode.SYSTEM_ERROR,
                    "vue构建完成，但未生成dist目录");

            // 将dist作为部署目录
            sourceDir = distDir;
            log.info("将dist目录作为部署目录:{}", sourceDir.getAbsolutePath());
        }

        // 复制文件到部署目录中
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败" + e.getMessage());
        }
        // 更新y应用的deploykey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean b = this.updateById(updateApp);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR, "更新应用信息失败");

        String appDeployUrl = String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
        generateAppScreenshotAsync(appId, appDeployUrl);

        // 返回url
        return appDeployUrl;
    }

    private void generateAppScreenshotAsync(Long appId, String appUrl) {
        // 使用虚拟线程进行截图
        Thread.startVirtualThread(() -> {
            // 调用截图服务并将截图上传
            String cosUrl = screenshotService.generateAndUploadScreenshot(appUrl);
            // 更新应用的封面
            App updateApp = new App();
            updateApp.setId(appId);
            updateApp.setCover(cosUrl);
            boolean updateById = this.updateById(updateApp);
            ThrowUtils.throwIf(!updateById, ErrorCode.SYSTEM_ERROR, "更新应用封面失败");
        });
    }

    @Override
    public Long createApp(AppAddRequest appAddRequest, User loginUser) {
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化prompt不能为空");
        String appName = appAddRequest.getAppName();
        if (StrUtil.isBlank(appName)) {
            appName = initPrompt.substring(0, Math.min(initPrompt.length(), 12));
            appAddRequest.setAppName(appName);
        }
        App app = new App();
        BeanUtils.copyProperties(appAddRequest, app);

        // 获取当前登录用户
        app.setUserId(loginUser.getId());
        CodeGenTypeEnum codeGenTypeEnum = aiCodeGenTypeRoutingService.routeCodeGenTypeEnum(initPrompt);
        app.setCodeGenType(codeGenTypeEnum.getValue());

        boolean save = this.save(app);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        log.info("应用创建成功，ID:{}，应用名称:{}，代码类型:{}", app.getId(), appName, codeGenTypeEnum.getValue());
        return app.getId();
    }

}
