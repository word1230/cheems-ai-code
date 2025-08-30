package com.cheems.cheemsaicode.aop;


import com.cheems.cheemsaicode.annotation.AuthCheck;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.model.entity.User;
import com.cheems.cheemsaicode.model.enums.UserRoleEnum;
import com.cheems.cheemsaicode.service.UserService;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;


    @Around("@annotation(authCheck)")
    public Object doIntercept(final ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {

        // 获取当前用户
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        // 拿到当前用户的权限
        String userRole = loginUser.getUserRole();
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(userRole);
        ThrowUtils.throwIf(userRoleEnum == null , ErrorCode.OPERATION_ERROR);
        // 与方法注解中的权限进行对比，
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum= UserRoleEnum.getEnumByValue(mustRole);
        ThrowUtils.throwIf(mustRoleEnum == null , ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(!userRoleEnum.hasPermission(mustRoleEnum), ErrorCode.NO_AUTH_ERROR);
        return  joinPoint.proceed();


    }


}
