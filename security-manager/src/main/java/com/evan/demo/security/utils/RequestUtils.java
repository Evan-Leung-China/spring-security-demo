package com.evan.demo.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

public class RequestUtils {

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpSession getCurrentSession() {
        HttpServletRequest currentRequest = getCurrentRequest();
        if (Objects.isNull(currentRequest)) {
            throw new RuntimeException("当前请求为空");
        }
        return currentRequest.getSession();
    }
}
