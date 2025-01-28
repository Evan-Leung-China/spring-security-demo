package com.evan.demo.security.config;

import com.evan.demo.security.core.authentication.UrlAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * spring security6.1.5 删除了WebSecurityConfigurerAdapter
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private final AuthorizationManager<RequestAuthorizationContext> authorizationManager;

    @Autowired
    public WebSecurityConfig(AuthorizationManager<RequestAuthorizationContext> rbacAuthorizationManager) {
        this.authorizationManager = rbacAuthorizationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UrlAuthorizationService urlAuthorizationService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
//                    authorize.anyRequest().access(authorizationManager);
                    Map<String, List<String>> uriRoleList = urlAuthorizationService.buildUrlAuthorizationMap();
                    if (CollectionUtils.isEmpty(uriRoleList)) {
                        return;
                    }
                    uriRoleList.forEach((uri, roles) -> {
                        authorize.requestMatchers(uri).hasAnyRole(roles.toArray(new String[0]));
                    });
                })
                .build();
    }

}
