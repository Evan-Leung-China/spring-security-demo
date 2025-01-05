package com.evan.example.web.core.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class SecuredBodyAuthenticationConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractAuthenticationFilterConfigurer<H, SecuredBodyAuthenticationConfigurer<H>, SecuredBodyAuthenticationFilter> {

    public SecuredBodyAuthenticationConfigurer() {
        super(new SecuredBodyAuthenticationFilter(), null);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, HttpMethod.POST.name());
    }

    public SecuredBodyAuthenticationConfigurer<H> setAesKey(String aesKey) {
        getAuthenticationFilter().setAesKey(aesKey);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(H http) {
        JsonAuthenticationEntryPoint authenticationEntryPoint = new JsonAuthenticationEntryPoint();
        super.registerAuthenticationEntryPoint(http, authenticationEntryPoint);

        KaltAuthenticationProvider kaltAuthenticationProvider = new KaltAuthenticationProvider();
        postProcess(kaltAuthenticationProvider);
        http.authenticationProvider(kaltAuthenticationProvider);

        LogoutConfigurer<H> logoutConfigurer = getBuilder().getConfigurer(LogoutConfigurer.class);
        logoutConfigurer.logoutSuccessHandler(new JsonLogoutSuccessHandler());

        SecurityContextRepository sharedObject = http.getSharedObject(SecurityContextRepository.class);
        if (Objects.nonNull(sharedObject)) {
            log.warn("theres's a SecurityContextRepository already, witch is {}", sharedObject);
            return;
        }

        SecurityContextHolderStrategy securityContextHolderStrategy = http.getSharedObject(SecurityContextHolderStrategy.class);
        if (Objects.isNull(securityContextHolderStrategy)) {
            securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
            http.setSharedObject(SecurityContextHolderStrategy.class, securityContextHolderStrategy);
        }
        UserSessionSecurityContextRepository securityContextRepository = new UserSessionSecurityContextRepository(securityContextHolderStrategy);
        http.setSharedObject(SecurityContextRepository.class, securityContextRepository);
    }

    @Override
    public void configure(H http) throws Exception {
        // UsernamePasswordAuthenticationFilter
        SecuredBodyAuthenticationFilter authFilter = super.getAuthenticationFilter();
        SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        authenticationSuccessHandler.setTargetUrlParameter("");
        authenticationSuccessHandler.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
                // 需要先将请求地址的前半部分
                try {
                    request.getRequestDispatcher(url).forward(request, response);
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        authFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        JsonAuthenticationFailureHandler authenticationFailureHandler = new JsonAuthenticationFailureHandler();
        authFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        RequestCache requestCache = http.getSharedObject(RequestCache.class);
        if (requestCache != null) {
            authenticationSuccessHandler.setRequestCache(requestCache);
        }
        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            authFilter.setRememberMeServices(rememberMeServices);
        }

        SecurityContextRepository securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
        authFilter.setSecurityContextRepository(securityContextRepository);

        authFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
        SecuredBodyAuthenticationFilter filter = postProcess(authFilter);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
