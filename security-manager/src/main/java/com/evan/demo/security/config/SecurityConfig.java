package com.evan.demo.security.config;

import com.evan.demo.security.core.CommonResult;
import com.evan.demo.security.core.ErrorCodeEnum;
import com.evan.demo.security.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private static final String SESSION_HEADER_NAME = "X-SESSION-ID";

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.setSharedObject(SecurityContextRepository.class, securityContextRepository());
        http.setSharedObject(SessionAuthenticationStrategy.class, sessionAuthenticationStrategy());

        return http
                .exceptionHandling(customizer ->
                        // 配置与异常处理的两种场景对应的处理组件
                        customizer.accessDeniedHandler(accessDeniedHandler)
                                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login.html")))
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
//                .rememberMe(customizer -> customizer.tokenValiditySeconds((int) Duration.ofDays(7).toSeconds())
//                        .tokenRepository(new InMemoryTokenRepositoryImpl())
//                        .userDetailsService(getUserDetailsService()))
                .authorizeHttpRequests(customizer -> customizer.requestMatchers("/login", "/login.html", "/captcha")
                        .permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new ChangeSessionIdAuthenticationStrategy();
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
    public HttpSessionSecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

}
