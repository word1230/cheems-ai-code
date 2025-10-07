package com.cheems.cheemsaicode.service;

import com.cheems.cheemsaicode.model.dto.app.AppQueryRequest;
import com.cheems.cheemsaicode.model.entity.User;
import com.cheems.cheemsaicode.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.cheems.cheemsaicode.model.entity.App;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author cheems
 */
public interface AppService extends IService<App> {

    /**
     * 校验应用参数
     * @param app
     * @param add 是否为创建
     */
    void validApp(App app, boolean add);

    /**
     * 获取应用VO
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    /**
     * 获取AppVO列表
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 构造查询参数
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);


    Flux<String> chatToGenCode(String userMessage, Long appId, User loginUser);


    String deployApp(Long appId, User loginUser);
}
