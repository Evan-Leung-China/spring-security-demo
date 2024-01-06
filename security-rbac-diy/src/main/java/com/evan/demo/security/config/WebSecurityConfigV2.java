package com.evan.demo.security.config;

import com.evan.demo.security.core.authorization.DevUserDetailDV2;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * spring security6.1.5 删除了WebSecurityConfigurerAdapter
 * <p>
 * 当前配置虽然能用，但如果配置session并发控制则该功能会不生效。因为我们先生成了认证过滤器，并为此提前创建了sessionAuthenticationStrategy。这导致配置并发控制时引入的sessionAuthenticationStrategy没有被认证过滤器所使用。
 * <p>如果我们希望并发控制生效，又想使用sessionManagementConfigurer进行配置，则不能这么做。需要自定义认证过滤器的Configurer。</p>
 * <p>如果我们希望并发控制生效，不使用sessionManagementConfigurer配置的话，则需要提前把相关的sessionAuthenticationStrategy配置好。</p>
 * <p>为了便于配置推荐使用自定义Configurer的方式。</p>
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "dev-security", name = "version", havingValue = "dv2")
public class WebSecurityConfigV2 {

    @Autowired
    private final AuthorizationManager<RequestAuthorizationContext> authorizationManager;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public WebSecurityConfigV2(AuthorizationManager<RequestAuthorizationContext> rbacAuthorizationManager, AuthenticationManager authenticationManager) {
        this.authorizationManager = rbacAuthorizationManager;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authenticationManager(authenticationManager)
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
//                .exceptionHandling(config -> {
//                    AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> response.getWriter().write(accessDeniedException.getMessage());
//                    config.accessDeniedHandler(accessDeniedHandler);
//                })
                // 配置登录请求页面，以便访问异常时重定向，会自动设置为UsernamePasswordTokenAuthenticationFilter处理的登录接口的
//                .exceptionHandling(config -> config.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/error").permitAll()
                        .anyRequest().access(authorizationManager))
                .sessionManagement(configurer -> configurer.maximumSessions(1)
                        .expiredSessionStrategy(event -> {
                            HttpServletResponse response1 = event.getResponse();
                            response1.setContentType("text/html;charset=utf-8");
                            response1.getWriter().print("<html><head><meta http-equiv='Content-Type' content='text/html;charset=utf-8' /></head>" +
                                    "<body><div>你的账号存在多处登录，请确认账号安全。必要时请修改密码！</div></body></html>");
                            response1.flushBuffer();
                        }))
                .build();
    }


    @Configuration
    @ConditionalOnProperty(prefix = "dev-security", name = "version", havingValue = "dv2")
    public static class AuthenticationManagerConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                UserDetailsService userDetailsService) {
            DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider() {
                @Override
                protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
                    // 校验验证码，这里建议使用地址栏传参，避免重复读取requestBody导致异常。
//                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//                    String kaptCode = request.getParameter("kaptCode");
                    logger.info("1. 验证码校验通过。");
                    // 校验密码
                    super.additionalAuthenticationChecks(userDetails, authentication);
                    logger.info("2. 用户账号校验通过。");
                    // 按照我们的常规思路，再来加载用户权限
                    logger.info("3. 加载用户权限");
                    DevUserDetailDV2 principal = (DevUserDetailDV2) userDetails;
                    principal.loadAuthorities();
                }
            };
            authenticationProvider.setUserDetailsService(userDetailsService);
            authenticationProvider.setPasswordEncoder(passwordEncoder());
            authenticationProvider.setPreAuthenticationChecks(new AccountStatusUserDetailsChecker());

            ProviderManager providerManager = new ProviderManager(authenticationProvider);
            providerManager.setEraseCredentialsAfterAuthentication(false);

            return providerManager;
        }
    }
}
