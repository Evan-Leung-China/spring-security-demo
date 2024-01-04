//package com.evan.demo.security.core.authentication;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
//import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
//import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
//import org.springframework.security.web.PortMapper;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
//import org.springframework.security.web.authentication.RememberMeServices;
//import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
//import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
//import org.springframework.security.web.context.SecurityContextRepository;
//import org.springframework.security.web.savedrequest.RequestCache;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//
//public final class AuthenticationWithKaptFilterConfigurer
//        extends AbstractAuthenticationFilterConfigurer<HttpSecurity,AuthenticationWithKaptFilterConfigurer, AuthenticationWithKaptFilter> {
//
//    public AuthenticationWithKaptFilterConfigurer() {
//        super(new AuthenticationWithKaptFilter(), null);
//        usernameParameter("username");
//        passwordParameter("password");
//    }
//
//    /**
//     * The HTTP parameter to look for the username when performing authentication. Default
//     * is "username".
//     * @param usernameParameter the HTTP parameter to look for the username when
//     * performing authentication
//     * @return the {@link FormLoginConfigurer} for additional customization
//     */
//    public AuthenticationWithKaptFilterConfigurer usernameParameter(String usernameParameter) {
//        getAuthenticationFilter().setUsernameParameter(usernameParameter);
//        return this;
//    }
//
//    /**
//     * The HTTP parameter to look for the password when performing authentication. Default
//     * is "password".
//     * @param passwordParameter the HTTP parameter to look for the password when
//     * performing authentication
//     * @return the {@link FormLoginConfigurer} for additional customization
//     */
//    public AuthenticationWithKaptFilterConfigurer passwordParameter(String passwordParameter) {
//        getAuthenticationFilter().setPasswordParameter(passwordParameter);
//        return this;
//    }
//
//    @Override
//    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
//        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
//    }
//
//    /**
//     * If available, initializes the {@link DefaultLoginPageGeneratingFilter} shared
//     * object.
//     * @param http the {@link HttpSecurityBuilder} to use
//     */
//    private void initDefaultLoginFilter(HttpSecurity http) {
//        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
//                .getSharedObject(DefaultLoginPageGeneratingFilter.class);
//        if (loginPageGeneratingFilter != null && !isCustomLoginPage()) {
//            loginPageGeneratingFilter.setFormLoginEnabled(true);
//            loginPageGeneratingFilter.setUsernameParameter(getUsernameParameter());
//            loginPageGeneratingFilter.setPasswordParameter(getPasswordParameter());
//            loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
//            loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
//            loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
//        }
//    }
//
//    /**
//     * Gets the HTTP parameter that is used to submit the username.
//     * @return the HTTP parameter that is used to submit the username
//     */
//    private String getUsernameParameter() {
//        return getAuthenticationFilter().getUsernameParameter();
//    }
//
//    /**
//     * Gets the HTTP parameter that is used to submit the password.
//     * @return the HTTP parameter that is used to submit the password
//     */
//    private String getPasswordParameter() {
//        return getAuthenticationFilter().getPasswordParameter();
//    }
//
//
//    @Override
//    public void init(HttpSecurity http) throws Exception {
//        super.init(http);
//        initDefaultLoginFilter(http);
//    }
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//
//        PortMapper portMapper = http.getSharedObject(PortMapper.class);
//        if (portMapper != null) {
//            ((LoginUrlAuthenticationEntryPoint)super.getAuthenticationEntryPoint()).setPortMapper(portMapper);
//        }
//        RequestCache requestCache = http.getSharedObject(RequestCache.class);
//        if (requestCache != null) {
//            ((LoginUrlAuthenticationEntryPoint)super.getH()).set(requestCache);
//        }
//        getAuthenticationFilter().setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
//        getAuthenticationFilter().setAuthenticationSuccessHandler(super.successHandler);
//        getAuthenticationFilter().setAuthenticationFailureHandler(super.failureHandler);
//        if (super.authenticationDetailsSource != null) {
//            getAuthenticationFilter().setAuthenticationDetailsSource(super.authenticationDetailsSource);
//        }
//        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
//                .getSharedObject(SessionAuthenticationStrategy.class);
//        if (sessionAuthenticationStrategy != null) {
//            getAuthenticationFilter().setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
//        }
//        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
//        if (rememberMeServices != null) {
//            getAuthenticationFilter().setRememberMeServices(rememberMeServices);
//        }
//        SecurityContextConfigurer securityContextConfigurer = http.getConfigurer(SecurityContextConfigurer.class);
//        if (securityContextConfigurer != null && securityContextConfigurer.isRequireExplicitSave()) {
//            SecurityContextRepository securityContextRepository = securityContextConfigurer
//                    .getSecurityContextRepository();
//            getAuthenticationFilter().setSecurityContextRepository(securityContextRepository);
//        }
//        getAuthenticationFilter().setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
//        F filter = postProcess(getAuthenticationFilter());
//        http.addFilter(filter);
//    }
//}
