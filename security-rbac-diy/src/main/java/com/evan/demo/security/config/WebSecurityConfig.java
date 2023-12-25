package com.evan.demo.security.config;

import com.evan.demo.security.core.authentication.AuthenticationWithKaptFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * spring security6.1.5 删除了WebSecurityConfigurerAdapter
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private final AuthorizationManager<RequestAuthorizationContext> authorizationManager;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public WebSecurityConfig(AuthorizationManager<RequestAuthorizationContext> rbacAuthorizationManager, AuthenticationManager authenticationManager) {
        this.authorizationManager = rbacAuthorizationManager;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        DefaultLoginPageGeneratingFilter loginPageFilter = new DefaultLoginPageGeneratingFilter(null);
        loginPageFilter.setFormLoginEnabled(true);
        loginPageFilter.setAuthenticationUrl("/login");
        loginPageFilter.setPasswordParameter("password");
        loginPageFilter.setUsernameParameter("username");

        // SecurityContextRepository需要被SecurityContextHolderFilter所共享，才能自动从其中获取到SecurityContext恢复到SecurityContextHolder
        SecurityContextRepository securityContextRepository = securityContextRepository();
        // RequestCache需要被ExceptionTranslateFilter共享，以便保存认证前的请求进行恢复。
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        SessionAuthenticationStrategy sessionAuthenticationStrategy = new SessionFixationProtectionStrategy();
        RequestCache requestCache = requestCache();
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setRequestCache(requestCache);
        successHandler.setDefaultTargetUrl("/login");

        http.setSharedObject(SecurityContextHolderStrategy.class, securityContextHolderStrategy);
        http.setSharedObject(SecurityContextRepository.class, securityContextRepository);
        http.setSharedObject(SessionAuthenticationStrategy.class, sessionAuthenticationStrategy);
        http.setSharedObject(RequestCache.class, requestCache);


        AuthenticationWithKaptFilter authenticationFilter = getAuthenticationWithKaptFilter(securityContextHolderStrategy,
                sessionAuthenticationStrategy, successHandler, sessionAuthenticationStrategy);

        return http
//                .formLogin(Customizer.withDefaults())
                .securityContext(config -> config.securityContextRepository(securityContextRepository))
//                .exceptionHandling(config -> {
//                    AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> response.getWriter().write(accessDeniedException.getMessage());
//                    config.accessDeniedHandler(accessDeniedHandler);
//                })
                // 配置登录请求页面，以便访问异常时重定向
                .exceptionHandling(config -> config.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login","/foo/login").permitAll()
                        .anyRequest().access(authorizationManager))
                .sessionManagement(configurer -> configurer.sessionAuthenticationStrategy(sessionAuthenticationStrategy))
                .addFilter(loginPageFilter)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(new DefaultLogoutPageGeneratingFilter())
                .build();
    }

    private AuthenticationWithKaptFilter getAuthenticationWithKaptFilter(SecurityContextHolderStrategy securityContextHolderStrategy, SessionAuthenticationStrategy sessionAuthenticationStrategy, SavedRequestAwareAuthenticationSuccessHandler successHandler, SessionAuthenticationStrategy authenticationStrategy) {
        AuthenticationWithKaptFilter authenticationFilter = new AuthenticationWithKaptFilter(this.authenticationManager);
//        authenticationFilter.setRememberMeServices();
        // 默认为SavedRequestAwareAuthenticationSuccessHandler,即重定向到认证前的请求.但如果没有设置handler的跳转
//        authenticationFilter.setAuthenticationSuccessHandler();
//         认证失败的处理，如果不配置，将会执行request.sendError()
        authenticationFilter.setAuthenticationFailureHandler((request, response, ex) ->
                response.getWriter().write(ex.getMessage()));
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
        authenticationFilter.setSecurityContextHolderStrategy(securityContextHolderStrategy);
        authenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setSecurityContextRepository(securityContextRepository());
        return authenticationFilter;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public RequestCache requestCache() {
        return new HttpSessionRequestCache();
    }

    @Configuration
    public static class AuthenticationManagerConfig{

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                UserDetailsService userDetailsService) {
            DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
            authenticationProvider.setUserDetailsService(userDetailsService);
            authenticationProvider.setPasswordEncoder(passwordEncoder());
            authenticationProvider.setPreAuthenticationChecks(new AccountStatusUserDetailsChecker());

            ProviderManager providerManager = new ProviderManager(authenticationProvider);
            providerManager.setEraseCredentialsAfterAuthentication(false);

            return providerManager;
        }
    }
}
