package com.evan.demo.security.config;

import com.evan.demo.security.system.authentication.AuthenticationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@AutoConfigureAfter(SecurityConfig.class)
public class AuthenticationServiceConfig {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    @Autowired
    private SecurityContextRepository securityContextRepository;
    @Autowired
    private SecurityContextHolderStrategy contextHolderStrategy;

    @PostConstruct
    public void initAuthenticationController() {
        // 这里初始化的是与其他Filter协同的组件，其他依赖都直接注入了
        if (sessionAuthenticationStrategy != null) {
            this.authenticationService.setSessionStrategy(sessionAuthenticationStrategy);
        }

//        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
//        if (rememberMeServices != null) {
//            this.authenticationController.setRememberMeServices(rememberMeServices);
//        }

        this.authenticationService.setSecurityContextRepository(this.securityContextRepository);

        this.authenticationService.setSecurityContextHolderStrategy(this.contextHolderStrategy);
    }
}
