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
import org.springframework.web.bind.annotation.PostMapping;
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
@RestController
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final AuthenticationSuccessHandler successHandler;


    @Autowired
    public LoginController(AuthenticationManager authenticationManager,
                           SecurityContextRepository securityContextRepository,
                           RequestCache requestCache) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        // 用于登录成功后，跳转到之前未登录前的请求。
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setRequestCache(requestCache);
        handler.setDefaultTargetUrl("/index.html");
        this.successHandler = handler;

    }

    /**
     * TODO 没有请求到这里
     * 这是表单参数，因此不能使用@RequestBody
     * 同时还丢失了自动重定向到原来的页面的功能
     * 因为这是基于Rest风格的，适用于前后端分离的场景。
     * 完了之后，如果是基于token进行校验，还应当自定义一个过滤器校验token。
     *
     * @param loginRequest
     * @return
     */
//    @PostMapping("/login")
    public ResponseEntity<String> login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 校验验证码 === verify kaptcha
        logger.info("1. 验证码验证成功！{}",loginRequest);
        // 2. 使用AuthenticationManager验证用户密码
        // 其实UsernamePasswordAuthenticationFilter的核心逻辑也就是封装好参数仅此而已，实现登录逻辑的还得是AuthenticationManager
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authentication =
                this.authenticationManager.authenticate(authenticationRequest);
        // 创建新的session
        // 这里偷个懒，没有使用SpringSecurity的SessionFixationProtectionStrategy
        // 但可能会影响原有的Spring相关组件的功能。因为我们只是简单粗暴地重建了session。而session里面的东西都没有拷贝出来。
//        request.getSession().invalidate();
//        request.getSession(true);
        // 3. 加载用户权限
        logger.info("3. 验证通过加载用户权限");
        // 创建安全上下文。安全上下文只对当前请求有用。因此需要配合SecurityContextRepository来进行上下文恢复。
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
//        SecurityContextHolder.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        // 登录成功后，自动跳转到尚未登录的请求。
        // 但如果这样的其实并没有必要使用这种自定义/login接口的方式。因为一般需要使用Rest方式进行自定义时，都是前后端分离的项目
        // 这个接口的返回值也就失去意义了
        successHandler.onAuthenticationSuccess(request, response, authenticationRequest);
        return ResponseEntity.ofNullable("ok");
    }

    public record LoginRequest(String username, String password, String kaptcha) {
    }
}
