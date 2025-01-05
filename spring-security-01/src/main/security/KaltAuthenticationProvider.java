package com.evan.example.web.core.security;

import com.evan.example.web.system.user.mapper.UserMapper;
import com.evan.example.web.system.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Objects;

@Slf4j
public class KaltAuthenticationProvider implements AuthenticationProvider {

    public static final String MOCK_KALT = "123456";
    public static final String MOCK_PASSWORD = "Aa123456";

    @Autowired
    private UserMapper userMapper;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        KaltAuthentication kaltAuthentication = (KaltAuthentication) authentication;
        String username = kaltAuthentication.getPrincipal();

        // 1. 校验验证码 -- mock
        if (!MOCK_KALT.equals(kaltAuthentication.getKalt())) {
            log.error("kalt error");
            throw new BadCredentialsException("验证码错误");
        }
        // 2. 查询用户
        User user = userMapper.selectByUsername(username);
        if (Objects.isNull(user)) {
            log.error("username:{} not found", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 3. 校验密码；这里可能存在数据库中密码字段加密的场景，可自行处理。SpringSecurity提供了PasswordEncoder处理的。
        if (!MOCK_PASSWORD.equals(kaltAuthentication.getCredentials())) {
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
        List<GrantedAuthority> authorities = userMapper.selectPermissionByUsername(username);

        KaltAuthentication authenticated = new KaltAuthentication(username, kaltAuthentication.getCredentials(), authorities);
        authenticated.setDetails(user);
        authenticated.setSalt(kaltAuthentication.getSalt());
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return KaltAuthentication.class.isAssignableFrom(authentication);
    }
}
