package com.evan.demo.security.core.authentication.v3;

import cn.hutool.core.collection.CollectionUtil;
import com.evan.demo.security.core.authentication.UrlAuthorizationService;
import com.evan.demo.security.system.pojo.dto.UpdateAuthorityDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
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
@ConditionalOnProperty(prefix = "dev-security", name = "version", havingValue = "v3")
public class RbacAuthorizationManagerV3 implements AuthorizationManager<RequestAuthorizationContext> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final AuthorizationDecision AUTHORIZED_DECISION = new AuthorizationDecision(true);


    private Map<AntPathRequestMatcher, List<ComplexGrantedAuthority>> requestRoleMap;

    @Autowired
    void initAuthorizationMap(UrlAuthorizationService urlAuthorizationService) {
        this.requestRoleMap = urlAuthorizationService.buildUrlAuthorizationMapV2()
                .entrySet().stream()
                .collect(Collectors.toMap(entry -> new AntPathRequestMatcher(entry.getKey()),
                        entry -> entry.getValue().stream().map(ComplexGrantedAuthority::new).toList())
                );
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestContext) {
        Authentication authenticate = authentication.get();
        if (authenticate == null || authenticate instanceof AnonymousAuthenticationToken) {
            logger.info("please authenticate first.");
            return new AuthorizationDecision(false);
        }
        // 寻找当前请求的URI应当有的权限
        HttpServletRequest request = requestContext.getRequest();
        Optional<List<ComplexGrantedAuthority>> grantedAuthorities = this.requestRoleMap.entrySet().stream()
                .filter(entry -> entry.getKey().matches(request))
                .findAny()
                .map(Map.Entry::getValue);
        if (grantedAuthorities.isEmpty()) {
            logger.info("due to no authorities required for uri:{}, granted", request.getRequestURI());
            return AUTHORIZED_DECISION;
        }
        // 与当前用户所拥有的权限进行比较
        boolean granted = CollectionUtil.containsAny(authenticate.getAuthorities(), grantedAuthorities.get());
        if (!granted) {
            logger.info("access {} deny. not efficient privilege for {}.", request.getRequestURI(), authenticate.getName());
        }
        return new AuthorizationDecision(granted);
    }

    /**
     * 菜单权限更新
     */
    public void updateAuthorities(List<UpdateAuthorityDTO> updateAuthorityList) {
        Map<String, List<ComplexGrantedAuthority>> uriPatternMap = this.requestRoleMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getPattern(), Map.Entry::getValue));
        updateAuthorityList.forEach(authority -> {
            List<ComplexGrantedAuthority> grantedAuthorityList = uriPatternMap.get(authority.getMenuUri());
            if (Objects.isNull(grantedAuthorityList)) {
                // 一般认为接口至少会授权给超级管理员角色，所以一定会被加载到这里的。不存在则异常,不进行处理，
                return;
            }
            ComplexGrantedAuthority grantedAuthority = new ComplexGrantedAuthority(authority.getRoleId());
            switch (authority.getModifyType()) {
                case ADD -> grantedAuthorityList.add(grantedAuthority);
                case DELETE -> grantedAuthorityList.remove(grantedAuthority);
                // 只有新增或者删除接口权限
//                case UPDATE ->
            }
        });
    }

}
