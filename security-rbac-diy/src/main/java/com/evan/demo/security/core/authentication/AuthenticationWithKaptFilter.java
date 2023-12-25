package com.evan.demo.security.core.authentication;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

public class AuthenticationWithKaptFilter extends AbstractAuthenticationProcessingFilter {
    public AuthenticationWithKaptFilter(AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/login", "POST"), authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // 如果使用的body传参，则通过这个获取：
        // JSON.parseObject(request.getInputStream(), LoginDTO.class);
        LoginDTO loginData = new LoginDTO();
        loginData.setPassword(request.getParameter("password"));
        loginData.setUsername(request.getParameter("username"));
        // 验证码校验通过
        logger.info("模拟图形验证码验证通过！");
        logger.info("正在进行账号密码校验。。。");
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginData.getUsername(),
                loginData.getPassword());
        Authentication authenticate = getAuthenticationManager().authenticate(authRequest);
        logger.info("认证成功!");
        return authenticate;
    }

    class LoginDTO{
        private String username;
        private String password;
        /**
         * 验证码
         */
        private String kaptCode;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getKaptCode() {
            return kaptCode;
        }

        public void setKaptCode(String kaptCode) {
            this.kaptCode = kaptCode;
        }
    }
}
