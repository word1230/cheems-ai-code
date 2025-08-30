package com.cheems.cheemsaicode.service;

import com.cheems.cheemsaicode.model.vo.LoginUserVO;
import com.mybatisflex.core.service.IService;
import com.cheems.cheemsaicode.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 *  服务层。
 *
 * @author cheems
 */
public interface UserService extends IService<User> {


    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    LoginUserVO login(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 获取用户脱敏信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);
}
