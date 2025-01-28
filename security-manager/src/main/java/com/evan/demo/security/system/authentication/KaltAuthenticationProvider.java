package com.evan.demo.security.system.authentication;

import com.evan.demo.security.system.user.AuthorityService;
import com.evan.demo.security.system.user.dao.DevUserRepository;
import com.evan.demo.security.system.user.pojo.entity.DevUser;
import com.evan.demo.security.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Authentication架构中，有如下几个组件：
 * <p>AuthenticationManager</p>
 * <p>AuthenticationProvider</p>
 * 选择自定义AuthenticationProvider的原因：是因为我们不希望在通过UserDetailsService加载用户时就直接加载权限。
 * 此外，还有一点就是，Spring还有其他默认的AuthenticationProvider，比如：AnonymousAuthenticationProvider。
 * 这些都是由ProviderManager来管理的。如果我们直接自定义，还要处理这些，比较麻烦。
 * 但选择自定义AuthenticationProvider也会带来一个麻烦，就是RememberMe的时候，做自动登录时，需要加载权限。意味着我们也需要定制RememberMeServices。
 */
@Slf4j
@Component
public class KaltAuthenticationProvider implements AuthenticationProvider {

    public static final String MOCK_PASSWORD = "Aa123456";

    @Autowired
    private DevUserRepository userRepository;
    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private CaptchaService captchaService;

    @Override
    public KaltAuthentication authenticate(Authentication authentication) throws AuthenticationException {
        KaltAuthentication kaltAuthentication = (KaltAuthentication) authentication;
        String username = kaltAuthentication.getPrincipal();

        // 1. 校验验证码 -- mock
        if (!captchaService.validateCaptcha(RequestUtils.getCurrentSession(), kaltAuthentication.getKalt())) {
            log.error("kalt error");
            throw new BadCredentialsException("验证码错误");
        }
        // 2. 查询用户
        DevUser user = userRepository.findByUserName(username);
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
        List<GrantedAuthority> authorities = authorityService.selectByRoleIds(List.of(user.getId()));

        return KaltAuthentication.builder()
                .principal(username)
                .credentials(kaltAuthentication.getCredentials())
                .kalt(kaltAuthentication.getKalt())
                .authorities(authorities)
                .authenticated(true)
                .details(user)
                .build();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return KaltAuthentication.class.isAssignableFrom(authentication);
    }
}
