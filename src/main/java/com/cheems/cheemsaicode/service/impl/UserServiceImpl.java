package com.cheems.cheemsaicode.service.impl;

import ch.qos.logback.core.util.MD5Util;
import cn.hutool.core.util.StrUtil;
import com.cheems.cheemsaicode.constant.UserConstant;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.model.vo.LoginUserVO;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.cheems.cheemsaicode.model.entity.User;
import com.cheems.cheemsaicode.mapper.UserMapper;
import com.cheems.cheemsaicode.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 *  服务层实现。
 *
 * @author cheems
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{


    @Resource
    private UserMapper userMapper;


    @Override
    public LoginUserVO login(String userAccount, String userPassword, HttpServletRequest request) {

        //校验参数
        ThrowUtils.throwIf(StrUtil.isAllBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");
        ThrowUtils.throwIf(userAccount.length()<4, ErrorCode.PARAMS_ERROR, "账号长度不能小于4");
        ThrowUtils.throwIf(userPassword.length()<8, ErrorCode.PARAMS_ERROR, "密码长度不能小于8");

        // 构造查询条件

        String encryPassword = this.getEncryPassword(userPassword);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);

        ThrowUtils.throwIf(user==null, ErrorCode.NOT_FOUND_ERROR, "用户不存在或密码错误");

        // 记录用户登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);

        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {

      User loginUser = (User)  request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
      ThrowUtils.throwIf(loginUser==null|| loginUser.getId() == null, ErrorCode.NOT_FOUND_ERROR );
      //从数据库中查询用户信息
        User user = this.getById(loginUser.getId());
      ThrowUtils.throwIf(user==null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
      return user;
    }



    public String getEncryPassword(String userPassword){
        return DigestUtils.md5DigestAsHex(userPassword.getBytes());
    }


    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user==null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }
}
