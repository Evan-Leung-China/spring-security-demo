package com.evan.example.web.config;

import com.evan.example.web.core.CommonResult;
import com.evan.example.web.core.ErrorCodeEnum;
import com.evan.example.web.system.authentication.AuthenticationController;
import com.evan.example.web.utils.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private static final String SESSION_HEADER_NAME = "X-SESSION-ID";

    @Autowired
    private AuthenticationController authenticationController;

    /**
     * Spring Security相关配置
     */
    @Bean
    public SecurityContextHolderStrategy contextHolderStrategy() {
        // 指定使用可跨线程的ThreadLocal
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return SecurityContextHolder.getContextHolderStrategy();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.setSharedObject(SecurityContextRepository.class, securityContextRepository());

        DefaultSecurityFilterChain securityFilterChain = http
                .exceptionHandling(customizer ->
                        // 配置与异常处理的两种场景对应的处理组件
                        customizer.accessDeniedHandler(jsonAccessDeniedHandler())
                                .authenticationEntryPoint(jsonAuthenticationEntryPoint()))
                .sessionManagement(customizer ->
                        customizer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredSessionStrategy(event ->
                                // 这里我们需要返回给前端错误码
                                ResponseUtils.sendResponse(event.getRequest(), event.getResponse(), CommonResult.buildFailure(ErrorCodeEnum.SESSION_EXPIRED))
                        )
                        // 这个是当超过最大session后，允许当前用户登录，并将访问最少的session设置为超时
                        // 设置为true后，将会抛出登录异常，阻断当前登录
                        .maxSessionsPreventsLogin(false))
                .securityContext(customizer -> customizer.securityContextRepository(securityContextRepository()))
                .logout(customizer -> customizer.logoutUrl("/logout"))
                .csrf(AbstractHttpConfigurer::disable)
                .rememberMe(customizer -> customizer.tokenValiditySeconds((int) Duration.ofDays(7).toSeconds()))
                .authorizeHttpRequests(customizer -> customizer.requestMatchers("/login")
                        .permitAll()
                        .anyRequest().authenticated())
                .build();

        initAuthenticationController(http);

        return securityFilterChain;
    }

    @Bean
    public AccessDeniedHandler jsonAccessDeniedHandler() {
        return new AccessDeniedHandler() {
            private final Logger logger = LoggerFactory.getLogger("JsonAccessDeniedHandler");

            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                logger.error("ex on request:{}", request.getRequestURI());
                logger.error(accessDeniedException.getMessage(), accessDeniedException);
                ResponseUtils.sendResponse(request, response, CommonResult.buildFailure(ErrorCodeEnum.ACCESS_DENIED_ERROR));
            }
        };
    }

    /**
     * <p>Spring Security的异常处理配置</p>
     *
     * <p>1. Spring Security的异常处理原理</p>
     * 通过控制Filter的顺序来实现的
     * 这里主要是访问异常，例如：没有登录、权限不足
     * 因此，通过定义一个过滤器捕获chain.doFilter的异常
     * 同时需要将该过滤器的执行顺序优先于鉴权过滤器。
     *
     * <p>2. 这里配置的场景是：还没登录</p>
     *
     * @see org.springframework.security.config.annotation.web.builders.FilterOrderRegistration.FilterOrderRegistration
     */
    @SuppressWarnings("all")
    @Bean
    public AuthenticationEntryPoint jsonAuthenticationEntryPoint() {
        return new AuthenticationEntryPoint() {

            private final Logger logger = LoggerFactory.getLogger("JsonAuthenticationEntryPoint");

            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                logger.error("ex on request:{}", request.getRequestURI());
                logger.error(authException.getMessage(), authException);
                ResponseUtils.sendResponse(request, response, CommonResult.buildFailure(ErrorCodeEnum.AUTHENTICATION_REQUIRED));
            }
        };
    }

    private void initAuthenticationController(HttpSecurity http) {
        // 这里初始化的是与其他Filter协同的组件，其他依赖都直接注入了
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            this.authenticationController.setSessionStrategy(sessionAuthenticationStrategy);
        }

//        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
//        if (rememberMeServices != null) {
//            this.authenticationController.setRememberMeServices(rememberMeServices);
//        }

        this.authenticationController.setSecurityContextRepository(this.securityContextRepository());

        this.authenticationController.setSecurityContextHolderStrategy(this.contextHolderStrategy());
    }

    @Bean
    public HttpSessionSecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * spring session 相关配置
     */
    @Bean
    public HttpSessionIdResolver sessionIdResolver() {
        // 从请求头获取sessionId
        return new HeaderHttpSessionIdResolver(SESSION_HEADER_NAME);
    }

    @Bean
    public MapSessionRepository sessionRepository() {
        // 单一实例运行
        // 多实例的可以依赖spring-boot-starter-data-redis，用Redis的实现
        return new MapSessionRepository(new ConcurrentHashMap<>(128));
    }

}
