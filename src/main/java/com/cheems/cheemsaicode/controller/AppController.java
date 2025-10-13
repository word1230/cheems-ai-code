package com.cheems.cheemsaicode.controller;

import cn.hutool.json.JSONUtil;
import com.cheems.cheemsaicode.annotation.AuthCheck;
import com.cheems.cheemsaicode.common.BaseResponse;
import com.cheems.cheemsaicode.common.DeleteRequest;
import com.cheems.cheemsaicode.constant.AppConstant;
import com.cheems.cheemsaicode.constant.UserConstant;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.model.dto.app.*;
import com.cheems.cheemsaicode.model.entity.App;
import com.cheems.cheemsaicode.model.entity.User;
import com.cheems.cheemsaicode.model.vo.AppVO;
import com.cheems.cheemsaicode.service.AppService;
import com.cheems.cheemsaicode.service.ChatHistoryService;
import com.cheems.cheemsaicode.service.ProjectDownloadService;
import com.cheems.cheemsaicode.service.UserService;
import com.cheems.cheemsaicode.utils.ResultUtils;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 应用 控制层。
 *
 * @author cheems
 */
@RestController
@RequestMapping("/app")
@Slf4j
public class AppController {

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private ProjectDownloadService projectDownloadService;

    /**
     * 【用户】下载应用代码
     * 
     * @param appId
     * @param request
     * @param response
     */
    @GetMapping("/download/{appId}")
    public void downloadAppCode(@PathVariable Long appId, HttpServletRequest request, HttpServletResponse response) {
        // 1. 基础校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        // 2. 查询应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 权限校验
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "您不是应用创建者，无法下载代码");
        // 构建应用代码目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 检查代码目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "代码目录不存在");
        }
        // 生成下载的文件名
        String downloadFileName = String.valueOf(appId);
        // 调用下载服务
        projectDownloadService.downloadProjectAsZip(sourceDirPath, downloadFileName, response);
    }

    /**
     * 【用户】初始化对话会话
     * 判断是否首次进入应用，如果是首次进入且是应用创建者，返回初始化提示词
     *
     * @param appId
     *            应用ID
     * @param request
     *            请求
     * @return 初始化信息（包含是否首次访问、是否发送初始化提示词）
     */
    @GetMapping("/chat/init")
    public BaseResponse<Map<String, Object>> initChat(Long appId, HttpServletRequest request) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        User loginUser = userService.getLoginUser(request);

        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 判断是否首次访问
        boolean isFirstVisit = chatHistoryService.isFirstVisit(appId, loginUser.getId());

        // 只有创建者首次访问时才发送初始化提示词
        boolean shouldSendInitPrompt = isFirstVisit && app.getUserId().equals(loginUser.getId());

        Map<String, Object> result = Map.of(
                "isFirstVisit", isFirstVisit,
                "shouldSendInitPrompt", shouldSendInitPrompt,
                "initPrompt", shouldSendInitPrompt ? app.getInitPrompt() : "");

        return ResultUtils.success(result);
    }

    /**
     * 【用户】通过用户输入，生成代码
     *
     * @param userMessage
     * @param appId
     * @param request
     * @return
     */
    @GetMapping(path = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(String userMessage, Long appId, HttpServletRequest request) {

        try {
            ThrowUtils.throwIf(userMessage == null || userMessage.length() == 0, ErrorCode.PARAMS_ERROR, "用户消息不能为空");
            ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
            User loginUser = userService.getLoginUser(request);
            Flux<String> stringFlux = appService.chatToGenCode(userMessage, appId, loginUser);

            return stringFlux
                    .map(chunk -> {
                        Map<String, String> wrapper = Map.of("data", chunk);
                        String jsonData = JSONUtil.toJsonStr(wrapper);
                        return ServerSentEvent.<String>builder()
                                .data(jsonData)
                                .build();
                    })
                    .concatWith(Mono.just(
                            ServerSentEvent.<String>builder()
                                    .event("done")
                                    .data("")
                                    .build()))
                    .onErrorResume(Exception.class, e -> {
                        // 处理异常，返回流式格式的错误信息
                        log.error("流式代码生成异常", e);
                        String errorMessage;
                        if (e.getCause() instanceof dev.langchain4j.exception.RateLimitException) {
                            errorMessage = "请求过于频繁，请稍后再试。API 限制：" + e.getCause().getMessage();
                        } else {
                            errorMessage = "生成失败：" + e.getMessage();
                        }
                        Map<String, String> errorWrapper = Map.of("error", errorMessage);
                        String jsonData = JSONUtil.toJsonStr(errorWrapper);

                        return Flux.concat(
                            Mono.just(ServerSentEvent.<String>builder()
                                    .data(jsonData)
                                    .build()),
                            Mono.just(ServerSentEvent.<String>builder()
                                    .event("error")
                                    .data("")
                                    .build())
                        );
                    });
        } catch (Exception e) {
            // 处理前置校验异常
            log.error("代码生成前置校验失败", e);
            String errorMessage = "参数校验失败：" + e.getMessage();
            Map<String, String> errorWrapper = Map.of("error", errorMessage);
            String jsonData = JSONUtil.toJsonStr(errorWrapper);

            return Flux.concat(
                Mono.just(ServerSentEvent.<String>builder()
                        .data(jsonData)
                        .build()),
                Mono.just(ServerSentEvent.<String>builder()
                        .event("error")
                        .data("")
                        .build())
            );
        }
    }

    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appDeployRequest == null || appDeployRequest.getAppId() == null, ErrorCode.PARAMS_ERROR);
        Long appId = appDeployRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        User loginUser = userService.getLoginUser(request);
        String deploy = appService.deployApp(appId, loginUser);
        return ResultUtils.success(deploy);
    }

    /**
     * 【用户】创建应用（须填写 initPrompt）
     *
     * @param appAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {

        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long appId = appService.createApp(appAddRequest, loginUser);
        return ResultUtils.success(appId);
    }

    /**
     * 【用户】根据 id 修改自己的应用（目前只支持修改应用名称）
     *
     * @param appEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editApp(@RequestBody AppEditRequest appEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appEditRequest == null || appEditRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        App app = new App();
        BeanUtils.copyProperties(appEditRequest, app);

        // 校验参数
        appService.validApp(app, false);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 判断是否存在
        App oldApp = appService.getById(appEditRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人可编辑
        if (!oldApp.getUserId().equals(loginUser.getId())) {
            throw new com.cheems.cheemsaicode.exception.BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean update = appService.updateById(app);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(update);
    }

    /**
     * 【用户】根据 id 删除自己的应用
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 判断是否存在
        App oldApp = appService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人可删除
        if (!oldApp.getUserId().equals(loginUser.getId())) {
            throw new com.cheems.cheemsaicode.exception.BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean delete = appService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!delete, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(delete);
    }

    /**
     * 【用户】根据 id 查看应用详情
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人可查看
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new com.cheems.cheemsaicode.exception.BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return ResultUtils.success(appService.getAppVO(app));
    }

    /**
     * 【用户】分页查询自己的应用列表（支持根据名称查询，每页最多 20 个）
     *
     * @param appQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list")
    public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        appQueryRequest.setUserId(loginUser.getId());

        int pageNum = appQueryRequest.getPageNum();
        int pageSize = Math.min(appQueryRequest.getPageSize(), 20); // 每页最多 20 个

        Page<App> page = appService.page(Page.of(pageNum, pageSize), appService.getQueryWrapper(appQueryRequest));
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, page.getTotalRow());

        List<AppVO> appVOList = appService.getAppVOList(page.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 【用户】分页查询精选的应用列表（支持根据名称查询，每页最多 20 个）
     *
     * @param appQueryRequest
     * @return
     */
    @PostMapping("/list")
    public BaseResponse<Page<AppVO>> listAppVOByPage(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);

        int pageNum = appQueryRequest.getPageNum();
        int pageSize = Math.min(appQueryRequest.getPageSize(), 20); // 每页最多 20 个

        // 查询精选应用（优先级大于 0 的）
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        queryWrapper.gt("priority", 0);

        Page<App> page = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, page.getTotalRow());

        List<AppVO> appVOList = appService.getAppVOList(page.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 【管理员】根据 id 删除任意应用
     *
     * @param deleteRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete/admin")
    public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        boolean delete = appService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!delete, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(delete);
    }

    /**
     * 【管理员】根据 id 更新任意应用（支持更新应用名称、应用封面、优先级）
     *
     * @param appUpdateRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest) {
        ThrowUtils.throwIf(appUpdateRequest == null || appUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        App app = new App();
        BeanUtils.copyProperties(appUpdateRequest, app);

        // 校验参数
        appService.validApp(app, false);

        boolean update = appService.updateById(app);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(update);
    }

    /**
     * 【管理员】分页查询应用列表（支持根据除时间外的任何字段查询，每页数量不限）
     *
     * @param appQueryRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/admin")
    public BaseResponse<Page<AppVO>> listAppVOByPageAdmin(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);

        int pageNum = appQueryRequest.getPageNum();
        int pageSize = appQueryRequest.getPageSize();

        Page<App> page = appService.page(Page.of(pageNum, pageSize), appService.getQueryWrapper(appQueryRequest));
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, page.getTotalRow());

        List<AppVO> appVOList = appService.getAppVOList(page.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 【管理员】根据 id 查看应用详情
     *
     * @param id
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get")
    public BaseResponse<App> getAppById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(app);
    }
}
