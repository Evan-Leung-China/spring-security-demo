package com.evan.demo.security.core.authentication;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 登录控制器
 * 直接实现/login，这虽然能充分控制实现逻辑，但也意味这需要自行实现众多逻辑。
 * UsernamePasswordAuthenticationFilter包括如下逻辑：
 * AuthenticationManager实现登录验证逻辑
 *
 *
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html#customize-global-authentication-manager>自定义全局AuthenticationManager</a>
 */
@Controller
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
