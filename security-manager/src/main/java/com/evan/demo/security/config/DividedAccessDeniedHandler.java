package com.evan.demo.security.config;

import com.evan.demo.security.core.CommonResult;
import com.evan.demo.security.core.ErrorCodeEnum;
import com.evan.demo.security.utils.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.io.IOException;

@Slf4j
@Component
public class DividedAccessDeniedHandler implements AccessDeniedHandler {
    @Lazy
    @Autowired
    private ResourceUrlProvider resourceUrlProvider;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String staticPath = getResourceUrlProvider().getForLookupPath(request.getRequestURI());
        if (StringUtils.isNotEmpty(staticPath) || StringUtils.startsWith(request.getRequestURI() , "/captcha")) {
            request.setAttribute("status", "403");
            request.setAttribute("message", "无权限访问");
            request.getRequestDispatcher("/error").forward(request, response);
            return;
        }
        log.error("ex on request:{}", request.getRequestURI());
        log.error(accessDeniedException.getMessage(), accessDeniedException);
        ResponseUtils.sendResponse(request, response, CommonResult.buildFailure(ErrorCodeEnum.ACCESS_DENIED_ERROR));
    }

    private ResourceUrlProvider getResourceUrlProvider() {
        return resourceUrlProvider;
    }
}
