package com.evan.example.web.config;

import com.evan.example.web.core.security.SecuredBodyAuthenticationConfigurer;
import com.evan.example.web.core.security.UserSessionSecurityContextRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private static final String SESSION_HEADER_NAME = "X-SESSION-ID";

    @Value("${security.authentication.aesKey:abcdef1234567890}")
    private String aesKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // rememberMe需要查询
        UserDetails admin = User.withUsername("ADMIN")
                .password(passwordEncoder.encode("ADMIN"))
                .authorities("system:user:query", "system:user:add", "system:user:edit")
                .build();

        UserDetails evan = User.withUsername("evan")
                .password(passwordEncoder.encode("evan1234"))
                .authorities("system:user:query")
                .build();
        return new InMemoryUserDetailsManager(Arrays.asList(admin, evan));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecuredBodyAuthenticationConfigurer<HttpSecurity> securedBodyAuthenticationConfigurer = new SecuredBodyAuthenticationConfigurer<HttpSecurity>()
                .setAesKey(aesKey)
                .loginProcessingUrl("/login");
        http.apply(securedBodyAuthenticationConfigurer);

        return http
                .exceptionHandling(AbstractHttpConfigurer::disable)
                .logout(customizer -> customizer.logoutUrl("/logout"))
                .csrf(AbstractHttpConfigurer::disable)
                .rememberMe(customizer -> customizer.tokenValiditySeconds((int) Duration.ofDays(7).toSeconds()))
                .authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())
                .build();
    }


    @Bean
    public HttpSessionIdResolver sessionIdResolver() {
        // 从请求头获取sessionId
        return new HeaderHttpSessionIdResolver(SESSION_HEADER_NAME);
    }

    @Bean
    public MapSessionRepository sessionRepository() {
        // 单一实例运行
        return new MapSessionRepository(new ConcurrentHashMap<>(128));
    }

}
