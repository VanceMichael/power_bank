package com.powerbank.config;

import com.powerbank.common.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 管理员权限校验切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AdminCheckAspect {

    @Before("@annotation(com.powerbank.config.RequireAdmin)")
    public void checkAdmin() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new BusinessException(403, "无权限访问");
        }
        HttpServletRequest request = attrs.getRequest();
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            throw new BusinessException(403, "无权限访问");
        }
    }
}
