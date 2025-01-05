package com.evan.example.web.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserSessionSecurityContextRepository implements SecurityContextRepository {
    public static final String HEADER_X_SECURED_ID = "X-SECURED-ID";
    static final String ATTRIBUTE_CONTEXT_ID = UserSessionSecurityContextRepository.class.getName();

    private final Map<String, SecurityContext> repository = new ConcurrentHashMap<>();


    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    public UserSessionSecurityContextRepository(SecurityContextHolderStrategy securityContextHolderStrategy) {
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        // 优先从前端设置的请求头中获取
        HttpServletRequest request = requestResponseHolder.getRequest();
        String sessionId = StringUtils.defaultString(request.getHeader(HEADER_X_SECURED_ID),
                (String) request.getAttribute(ATTRIBUTE_CONTEXT_ID));

        if (StringUtils.isEmpty(sessionId)) {
            log.warn("cannot find session id");
            return this.securityContextHolderStrategy.createEmptyContext();
        }

        SecurityContext securityContext = repository.get(sessionId);
        if (Objects.isNull(securityContext)) {
            return this.securityContextHolderStrategy.createEmptyContext();
        }
        return securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String contextId = generateContextId(request, response);
        repository.put(contextId, context);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String sessionId = (String) request.getAttribute(ATTRIBUTE_CONTEXT_ID);
        if (StringUtils.isEmpty(sessionId)) {
            return false;
        }
        return repository.containsKey(sessionId);
    }

    public String generateContextId(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = UUID.randomUUID().toString();
        // 这里是为了当前线程方便获取
        request.setAttribute(ATTRIBUTE_CONTEXT_ID, sessionId);
        // 这里是相当于给前端返回sessionId
        // 而前端每个请求都将这个sessionId放到Header中，
        // 也是这个请求头：UserSessionSecurityContextRepository.HEADER_X_SECURED_ID
        response.setHeader(HEADER_X_SECURED_ID, sessionId);

        return sessionId;
    }
}
