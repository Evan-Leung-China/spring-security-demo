package com.evan.demo.security.core.authorization;

import cn.hutool.core.collection.CollectionUtil;
import com.evan.demo.security.system.dao.DevRoleMenuRepositories;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * TODO
 */
@Component
public class RbacAuthorizationManagerV4 implements AuthorizationManager<RequestAuthorizationContext> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final AuthorizationDecision AUTHORIZED_DECISION = new AuthorizationDecision(true);


    private Set<MenuGrantedAuthority> grantedAuthoritiesList;

    @Autowired
    private MenuGrantedAuthorityManager grantedAuthorityManager;

    @Autowired
    void initAuthorizationMap(DevRoleMenuRepositories devRoleMenuRepositories) {
        this.grantedAuthoritiesList = devRoleMenuRepositories.queryAllUriRoleV4()
                .stream()
                .map(menu -> grantedAuthorityManager.getGrantedAuthority(menu.getId()))
                // 有些菜单可能是目录，没有对应的接口，因此需要过滤
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestContext) {
        HttpServletRequest request = requestContext.getRequest();
        Authentication authenticate = authentication.get();
        if (authenticate == null || authenticate instanceof AnonymousAuthenticationToken) {
            logger.info("please authenticate first.{}", request.getRequestURI());
            return new AuthorizationDecision(false);
        }
        // 寻找当前请求的URI应当有的权限
        List<MenuGrantedAuthority> matchedAuthorities = this.grantedAuthoritiesList.stream()
                .filter(menuGrantedAuthority -> menuGrantedAuthority.containsUri(request))
                .toList();
        if (CollectionUtil.isEmpty(matchedAuthorities)) {
            logger.info("due to no authorities required for uri:{}, granted", request.getRequestURI());
            return AUTHORIZED_DECISION;
        }
        boolean granted = CollectionUtil.containsAny(authenticate.getAuthorities(), matchedAuthorities);
        if (!granted) {
            logger.info("access {} deny. not efficient privilege for {}.", request.getRequestURI(), authenticate.getName());
        }
        return new AuthorizationDecision(granted);
    }

}
