package com.evan.demo.security.core.authentication.v2;

import cn.hutool.core.collection.CollectionUtil;
import com.evan.demo.security.core.authentication.UrlAuthorizationService;
import com.evan.demo.security.system.pojo.dto.UpdateAuthorityDTO;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * TODO
 */
@Component
@ConditionalOnProperty(prefix = "dev-security", name = "version", havingValue = "v2")
public class RbacAuthorizationManagerV2 implements AuthorizationManager<RequestAuthorizationContext> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final AuthorizationDecision AUTHORIZED_DECISION = new AuthorizationDecision(true);


    private Map<AntPathRequestMatcher, List<Integer>> requestRoleMap;

    @Autowired
    void initAuthorizationMap(UrlAuthorizationService urlAuthorizationService) {
        this.requestRoleMap = urlAuthorizationService.buildUrlAuthorizationMapV2()
                .entrySet().stream()
                .collect(Collectors.toMap(entry -> new AntPathRequestMatcher(entry.getKey()),
                        Map.Entry::getValue)
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
        Optional<List<Integer>> grantedAuthorities = this.requestRoleMap.entrySet().stream()
                .filter(entry -> entry.getKey().matches(request))
                .findAny()
                .map(Map.Entry::getValue);
        if (grantedAuthorities.isEmpty()) {
            logger.info("due to no authorities required for uri:{}, granted", request.getRequestURI());
            return AUTHORIZED_DECISION;
        }
        // 这里需要注意的是，由于我们是算是深度定制了，抛弃了GrantAuthority，所以不能直接通过authenticate.getAuthorities()来获取处理了。
        // 需要获取到当前用户再来处理。
        DevUserDetailV2 principal = (DevUserDetailV2) authenticate.getPrincipal();
        // 与当前用户所拥有的权限进行比较
        boolean granted = CollectionUtil.containsAny(principal.getRoleIdList(), grantedAuthorities.get());
        if (!granted) {
            logger.info("access deny. not efficient privilege for {}.", authenticate.getName());
        }
        return new AuthorizationDecision(granted);
    }

    /**
     * 菜单权限更新
     */
    public void updateAuthorities(List<UpdateAuthorityDTO> updateAuthorityList) {
        Map<String, List<Integer>> uriPatternMap = this.requestRoleMap.keySet().stream()
                .collect(Collectors.toMap(AntPathRequestMatcher::getPattern, ant -> this.requestRoleMap.get(ant)));
        updateAuthorityList.forEach(authority -> {
            List<Integer> grantedAuthorityList = uriPatternMap.get(authority.getMenuUri());
            if (Objects.isNull(grantedAuthorityList)) {
                // 一般认为接口至少会授权给超级管理员角色，所以一定会被加载到这里的。不存在则异常,不进行处理，
                return;
            }
            switch (authority.getModifyType()) {
                case ADD -> grantedAuthorityList.add(authority.getRoleId());
                case DELETE -> grantedAuthorityList.remove(authority.getRoleId());
                // 只有新增或者删除接口权限
//                case UPDATE ->
            }
        });
    }

}
