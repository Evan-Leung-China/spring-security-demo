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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

/**
 * spring security6.1.5 删除了WebSecurityConfigurerAdapter
 * <p>
 * 当前配置虽然能用，但如果配置session并发控制则该功能会不生效。因为我们先生成了认证过滤器，并为此提前创建了sessionAuthenticationStrategy。这导致配置并发控制时引入的sessionAuthenticationStrategy没有被认证过滤器所使用。
 * <p>如果我们希望并发控制生效，又想使用sessionManagementConfigurer进行配置，则不能这么做。需要自定义认证过滤器的Configurer。</p>
 * <p>如果我们希望并发控制生效，不使用sessionManagementConfigurer配置的话，则需要提前把相关的sessionAuthenticationStrategy配置好。</p>
 * <p>为了便于配置推荐使用自定义Configurer的方式。</p>
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
        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();

        SessionAuthenticationStrategy sessionAuthenticationStrategy = new CompositeSessionAuthenticationStrategy(
                List.of(new SessionFixationProtectionStrategy(),
                        new RegisterSessionAuthenticationStrategy(sessionRegistry),
                        new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry))
        );

        RequestCache requestCache = requestCache();
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setRequestCache(requestCache);
        successHandler.setDefaultTargetUrl("/");

        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

        http.setSharedObject(SecurityContextHolderStrategy.class, securityContextHolderStrategy);
        http.setSharedObject(SecurityContextRepository.class, securityContextRepository);
        http.setSharedObject(SessionAuthenticationStrategy.class, sessionAuthenticationStrategy);
        http.setSharedObject(RequestCache.class, requestCache);
        http.setSharedObject(SessionRegistry.class, sessionRegistry);


        AuthenticationWithKaptFilter authenticationFilter = getAuthenticationWithKaptFilter(securityContextHolderStrategy,
                sessionAuthenticationStrategy, successHandler, sessionAuthenticationStrategy);
        authenticationFilter.setPasswordParameter("password");
        authenticationFilter.setUsernameParameter("username");

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
                        .requestMatchers("/login", "/foo/login").permitAll()
                        .anyRequest().access(authorizationManager))
                .sessionManagement(configurer -> configurer
//                        .sessionAuthenticationStrategy(sessionAuthenticationStrategy)
                        // 此配置要求在其他地方显式调用sessionAuthenticationStrategy，同时不能设置sessionAuthenticationStrategy
                        // 因为他相当于不使用SessionManagerFilter管理session
                        // 而且会导致session并发控制失效，因为我们时提前生成好了sessionAuthenticationStrategy，除非我们手动的将这些设置好。
                        // 但是，还需要手动引入session并发控制过滤器。否则如果又使用sessionConfigurer进行设置，会导致使用的sessionAuthenticationStrategy与认证过滤器不同而导致问题。
                        .requireExplicitAuthenticationStrategy(true))
                .addFilter(loginPageFilter)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(new DefaultLogoutPageGeneratingFilter())
                .addFilter(new ConcurrentSessionFilter(sessionRegistry))
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
    public static class AuthenticationManagerConfig {

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
