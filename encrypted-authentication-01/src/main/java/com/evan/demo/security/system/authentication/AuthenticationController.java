package com.evan.demo.security.system.authentication;

import com.evan.demo.security.system.user.dto.request.ReqLoginDTO;
import com.evan.demo.security.core.CommonResult;
import com.evan.demo.security.core.ErrorCodeEnum;
import com.evan.demo.security.system.user.mapper.UserMapper;
import com.evan.demo.security.system.user.model.User;
import com.evan.demo.security.utils.AesUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Setter
@RestController
public class AuthenticationController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${security.authentication.aesKey:abcdef1234567890}")
    private String aesKey;

    public static final String MOCK_KALT = "123456";
    public static final String MOCK_PASSWORD = "Aa123456";

    @Autowired
    private UserMapper authenticationUserMapper;
    @Autowired
    private ObjectMapper objectMapper;

    private SessionAuthenticationStrategy sessionStrategy;
    private RememberMeServices rememberMeServices;
    private SecurityContextHolderStrategy securityContextHolderStrategy;
    private HttpSessionSecurityContextRepository securityContextRepository;

    @PostMapping("/login")
    public CommonResult<String> login(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        ReqLoginDTO loginRequest;
        try {
            // 解析解密请求体
            loginRequest = parseAndDecryptBody(requestBody);
        } catch (BadCredentialsException e) {
            return CommonResult.buildFailure(ErrorCodeEnum.AUTHENTICATION_ERROR, e.getMessage(), String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.buildFailure(ErrorCodeEnum.ERROR, "解析请求体失败", String.class);
        }

        try {
            // 1. 认证
            KaltAuthentication authResult = doAuthenticated(loginRequest);

            // 2. 认证成功后，执行session相关操作
            afterAuthenticated(request, response, authResult);

            // 模拟设置session
            request.getSession().setAttribute("hairCutMaster", "tony");

            // 3. 加密返回
            String jsonResponse = objectMapper.writer().writeValueAsString(authResult.getDetails());
            String encryptResponse = AesUtils.encrypt(jsonResponse, aesKey);
            return CommonResult.buildSuccess(encryptResponse);
        } catch (Exception ex) {
            // Authentication failed
            logger.error(ex.getMessage(), ex);
//            this.securityContextHolderStrategy.clearContext();
            return CommonResult.buildFailure(ErrorCodeEnum.AUTHENTICATION_ERROR, String.class);
        }
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
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Set SecurityContextHolder to %s", authResult));
        }
//        this.rememberMeServices.loginSuccess(request, response, authResult);
    }

    private KaltAuthentication doAuthenticated(ReqLoginDTO loginRequest) {
        String username = loginRequest.getUsername();

        // 1. 校验验证码 -- mock
        if (!MOCK_KALT.equals(loginRequest.getKalt())) {
            log.error("kalt error");
            throw new BadCredentialsException("验证码错误");
        }
        // 2. 查询用户
        User user = authenticationUserMapper.selectByUsername(username);
        if (Objects.isNull(user)) {
            log.error("username:{} not found", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 3. 校验密码；这里可能存在数据库中密码字段加密的场景，可自行处理。SpringSecurity提供了PasswordEncoder处理的。
        if (!MOCK_PASSWORD.equals(loginRequest.getPassword())) {
            log.error("password error");
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 4. 加载权限 == 这里是我们与Spring Security不同的地方。Spring Security在查询用户时就需要加载权限了，这也是定制的好处。
        // 因为我们并不需要一个如此庞大的认证体系，我们的诉求是明确的，不需要兼容各种场景。
        // 对于UsernamePasswordAuthenticationFilter使用的ProviderManager来说，
        // 他包括了：
        // 认证事件发布、父子认证器、Provider抽象
        // 而Provider中，以DaoAuthenticationProvider为例，又出来UserDetails、UserDetailsService
        // 甚至UserDetailsChecker
        List<GrantedAuthority> authorities = authenticationUserMapper.selectPermissionByUsername(username);

        KaltAuthentication authResult = KaltAuthentication.builder()
                .principal(loginRequest.getUsername())
                .credentials(loginRequest.getPassword())
                .kalt(loginRequest.getKalt())
                .authenticated(true)
                .authorities(authorities)
                .details(user)
                .build();
        return authResult;
    }

    private ReqLoginDTO parseAndDecryptBody(String requestBody) {

        ReqLoginDTO loginRequest;
        loginRequest = Optional.ofNullable(requestBody)
                .map(encryptedBody -> AesUtils.decrypt(encryptedBody, aesKey))
                .filter(StringUtils::isNotEmpty)
                .map(jsonBody -> {
                    try {
                        return objectMapper.readerFor(ReqLoginDTO.class).readValue(jsonBody, ReqLoginDTO.class);
                    } catch (IOException e) {
                        throw new RuntimeException("解析失败", e);
                    }
                })
                .orElseThrow(() -> new BadCredentialsException("用户名或密码不能为空"));
        return loginRequest;
    }


}
