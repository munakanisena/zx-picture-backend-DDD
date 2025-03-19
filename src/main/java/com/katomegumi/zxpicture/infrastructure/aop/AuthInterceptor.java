package com.katomegumi.zxpicture.infrastructure.aop;

import com.katomegumi.zxpicture.infrastructure.annotation.AuthCheck;
import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.domain.user.valueobject.UserRoleEnum;
import com.katomegumi.zxpicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 通过springboot-aop切面 加上注解 进行身份校验
 */
@Component
@Slf4j
@Aspect
public class AuthInterceptor {
    @Resource
    private UserApplicationService userApplicationService;

    /**
     * 进行权限效验
     * @param joinPoint
     * @param authCheck
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint,AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole(); //获得注解属性
        //获取request 用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        //获取当前用户
        User user = userApplicationService.getLoginUser(request);

        UserRoleEnum mustRoleEnum = UserRoleEnum.getUserRoleEnum(mustRole);

        //如果为空 说明无需权限直接放行
        if (mustRoleEnum==null){
            return joinPoint.proceed();
        }
        //获取用户的枚举常量
        UserRoleEnum userRoleEnum = UserRoleEnum.getUserRoleEnum(user.getUserRole());

        //为空进行拦截
        if (userRoleEnum==null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"需要登录");
        }

        // 要求必须有管理员权限，但用户没有管理员权限，拒绝
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
