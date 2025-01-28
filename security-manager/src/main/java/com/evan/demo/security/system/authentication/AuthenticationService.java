package com.evan.demo.security.system.authentication;

import com.evan.demo.security.system.user.pojo.dto.request.ReqLoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Setter
@Service
public class AuthenticationService {

    @Autowired
    private KaltAuthenticationProvider authenticationProvider;

    private SessionAuthenticationStrategy sessionStrategy;
    private RememberMeServices rememberMeServices;
    private SecurityContextHolderStrategy securityContextHolderStrategy;
    private SecurityContextRepository securityContextRepository;

    public KaltAuthentication authentication(HttpServletRequest request, HttpServletResponse response, ReqLoginDTO loginRequest) {
        // 1. 认证
        KaltAuthentication authResult = authenticationProvider.authenticate(toAuthentication(loginRequest));

        // 2. 认证成功后，执行session相关操作
        afterAuthenticated(request, response, authResult);

        return authResult;
    }

    private Authentication toAuthentication(ReqLoginDTO loginRequest) {
        return KaltAuthentication.builder()
                .principal(loginRequest.getUsername())
                .credentials(loginRequest.getPassword())
                .kalt(loginRequest.getCaptcha())
                .authenticated(false)
                .build();
    }

    private void afterAuthenticated(HttpServletRequest request, HttpServletResponse response, KaltAuthentication authResult) {
        // 设置到当前线程的ThreadLocal中，也可以不需要，因为我们没有其他逻辑要处理
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authResult);
//            this.securityContextHolderStrategy.setContext(context);

        // 调用session的相关逻辑，创建session和校验session数量
        this.sessionStrategy.onAuthentication(authResult, request, response);
        // 将认证的安全上下文保存到session中
        this.securityContextRepository.saveContext(context, request, response);
        if (log.isDebugEnabled()) {
            log.debug(String.format("Set SecurityContextHolder to %s", authResult));
        }
        this.rememberMeServices.loginSuccess(request, response, authResult);
    }
}
