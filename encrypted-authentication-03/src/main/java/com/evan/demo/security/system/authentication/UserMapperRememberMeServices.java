package com.evan.demo.security.system.authentication;

import com.evan.demo.security.system.user.mapper.AuthenticationMapper;
import com.evan.demo.security.system.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * RememberMe典型的应用是浏览器端的Web应用，基于Cookie来完成。
 * 这点也反映在Spring Security的RememberMeServices的实现类中。
 * <p>
 * RememberMeToken的组成部分
 * <p>原生的TokenBasedRememberMeServices，使用的组成如下：</p>
 * <p>1. 四段式</p>
 * token[0]-token[1]-token[2]-token[3]
 * username-expireTime-algorithm-signature
 * <p>2. 三段式</p>
 * token[0]-token[1]-token[2]
 * username-expireTime-signature
 * <p>但是，考虑到我们不存在cookie，而将token放到session中，又失去了rememberMe的作用（在session超时之后快速登录）</p>
 * <p>因此，我们需要使用基于数据库的方式。所以需要参考的是：PersistentTokenBasedRememberMeServices</p>
 * <p>PersistentTokenBasedRememberMeServices的token组成：</p>
 * <p>token[0]-token[1]</p>
 * <p>表主键-token</p>
 * <p>
 * 除此之外，我们还需要考虑，如果这个token怎么给到APP，APP怎么再传给我们。
 * 由于需要在互联网上传播，因此还需要考虑安全性。
 * 此外，该token一般有效时间比较长，一旦泄露，就会被盗取登录态。因此需要隐蔽。
 * cookie本质上就是header(Cookie)，而httpOnly，实际上是告诉浏览器，这个cookie的内容不要让js访问。
 * 这里我们先用header的方式实现，做个简单的demo。
 * 移动端设置的是RequestParameter：
 * 表主键-token-signature
 * <p>
 * 不过一般而言，移动端应用的话不会直接使用这种功能，我们平时的APP上也没有使用这种功能。
 * 而是使用生物识别的手段+token来实现重新登录/登录态恢复。
 *
 *
 * 执行流程如下：
 * 登录时需要告知是否要记住本次登录（通过地址栏参数remember-me=yes），
 * 在需要记住本次登录的情况下，登录成功后，将token放到header中，然后APP端通过header获取token。
 * APP在请求其他接口时，将token放到header（remember-me）中，然后APP端通过header获取token。
 * 然后通过检测请求头的token，来触发自动登录。
 * 请求头中的是rememberMeToken
 *
 * 自动登录流程：
 * 当检测没有登录态时，检查标识是否为remember-me，如果是，则自动登录。
 * 从header中获取token，并解析到token主键，从数据库中查询token。
 * 检查token的签名是否与数据库中的签名一致。
 * 如果一致，再检查tokenValue与数据库中是否一致。
 * 如果一致，则登录成功。
 *
 * 注意：这里只是演示效果，为了帮助大家理解这些功能和组件。
 * 实际上，APP端会结合生物识别手段，结合token，来恢复登录态。而不会自动恢复登录态。
 */
@Component
public class UserMapperRememberMeServices implements RememberMeServices, LogoutHandler {
    private static final String DELIMITER = "-";
    private static final int FIFTEEN_DAYS_IN_SECOND = 15 * 24 * 60 * 60;
    public static final String EMPTY_HEADER = "null";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String REMEMBER_ME_HEADER_NAME = "remember-me";

    public static final String DEFAULT_PARAMETER = "remember-me";
    private int tokenValiditySeconds = FIFTEEN_DAYS_IN_SECOND;
    private final AuthenticationMapper authenticationMapper;

    private static final TokenBasedRememberMeServices.RememberMeTokenAlgorithm ENCODING_ALGORITHM = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
    private final String key = "saltKey001";
    private boolean alwaysRemember;
    private final Random random = new Random();

    public UserMapperRememberMeServices(AuthenticationMapper authenticationMapper,
                                        @Value("${remember-me.always:false}") boolean alwaysRemember) {
        this.authenticationMapper = authenticationMapper;
        this.alwaysRemember = alwaysRemember;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        String rememberMeHeader = extractRememberMe(request);
        if (rememberMeHeader == null) {
            return null;
        }
        this.logger.debug("Remember-me session detected");
        if (StringUtils.isEmpty(rememberMeHeader)) {
            this.logger.debug("session was empty");
            return null;
        }
        // 创建HttpClient，通过Post请求，并设置Cookie
        try {

            String[] headerTokens = decodeToken(rememberMeHeader);
            User user = processAutoLoginCookie(headerTokens, request, response);
            this.checkUser(user);
            this.logger.debug("Remember-me cookie accepted");
            return createSuccessfulAuthentication(request, user);
        } catch (CookieTheftException ex) {
            cancelHeader(request, response);
            throw ex;
        } catch (UsernameNotFoundException ex) {
            this.logger.debug("Remember-me login was valid but corresponding user not found.", ex);
        } catch (InvalidCookieException ex) {
            this.logger.debug("Invalid remember-me cookie: " + ex.getMessage());
        } catch (AccountStatusException ex) {
            this.logger.debug("Invalid UserDetails: " + ex.getMessage());
        } catch (RememberMeAuthenticationException ex) {
            this.logger.debug(ex.getMessage());
        }
        cancelHeader(request, response);
        return null;
    }

    private void cancelHeader(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(REMEMBER_ME_HEADER_NAME, EMPTY_HEADER);
    }

    private void checkUser(User user) {
        // 检查用户状态
    }

    protected User processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
                                          HttpServletResponse response) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 + " tokens, but contained '"
                    + Arrays.asList(cookieTokens) + "'");
        }
        String presentedSeries = cookieTokens[0];
        String presentedToken = cookieTokens[1];
        RememberMeToken token = authenticationMapper.getTokenForSeries(presentedSeries);
        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        String signature = new RememberMeSignatureHelper(this.key, ENCODING_ALGORITHM.getDigestAlgorithm())
                .withUsername(token.getUsername())
                .withToken(presentedToken)
                .withDeadline(token.getDate())
                .makeTokenSignature();
        // 校验签名
        if (!signature.equals(token.getSignature())) {
            throw new RememberMeAuthenticationException("Remember-me token may be modified.");
        }

        // We have a match for this user/series combination
        if (!presentedToken.equals(token.getTokenValue())) {
            // Token doesn't match series value. Delete all logins for this user and throw
            // an exception to warn them.
            this.authenticationMapper.removeUserTokens(token.getUsername());
            throw new CookieTheftException(
                    "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.");
        }
        if (isTokenExpired(token.getDate().getTime())) {
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }
        // Token also matches, so login is valid. Update the token value, keeping the
        // *same* series number.
        this.logger.debug("Refreshing persistent login token for user '{}', series '{}'",
                token.getUsername(), token.getSeries());

        long expiryTime = calculateTokenExpiryTime();
        String newTokenSignature = new RememberMeSignatureHelper(this.key, ENCODING_ALGORITHM.getDigestAlgorithm())
                .withUsername(token.getUsername())
                .withToken(generateTokenData())
                .withDeadline(expiryTime)
                .makeTokenSignature();
        RememberMeToken newToken = new RememberMeToken(token.getUsername(), token.getSeries(),
                generateTokenData(), new Date(expiryTime), newTokenSignature);
        try {
            this.authenticationMapper.updateToken(newToken.getSeries(), newToken);
            addHeader(newToken, request, response);
        } catch (Exception ex) {
            this.logger.error("Failed to update token: ", ex);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
        }

        return authenticationMapper.selectByUsername(token.getUsername());
    }

    public void addHeader(RememberMeToken token, HttpServletRequest request,
                          HttpServletResponse response) {
        String data = token.getSeries() + DELIMITER + token.getTokenValue();
        logger.info("remember-me Series: {}, Token:{}", token.getSeries(), token.getTokenValue());
        String encodedData = new String(Base64.getEncoder().encode(data.getBytes()));
        response.setHeader(REMEMBER_ME_HEADER_NAME, encodedData);
    }

    private String[] decodeToken(String rememberMeHeader) {
        if (StringUtils.equals(EMPTY_HEADER, rememberMeHeader)) {
            return new String[0];
        }
        byte[] decoder = Base64.getDecoder().decode(rememberMeHeader);
        logger.info("remember-me decode: {}", new String(decoder));
        return StringUtils.split(new String(decoder), DELIMITER);
    }

    protected String generateSeriesData() {
        byte[] newSeries = new byte[16];
        this.random.nextBytes(newSeries);
        return new String(Base64.getEncoder().encode(newSeries));
    }

    protected String generateTokenData() {
        byte[] newToken = new byte[16];
        this.random.nextBytes(newToken);
        return new String(Base64.getEncoder().encode(newToken));
    }

    protected String extractRememberMe(HttpServletRequest request) {
        Object rememberMe = request.getHeader(REMEMBER_ME_HEADER_NAME);
        if (Objects.isNull(rememberMe)) {
            return null;
        }
        return rememberMe.toString();
    }

    protected Authentication createSuccessfulAuthentication(HttpServletRequest request, User user) {
        List<GrantedAuthority> authorities = authenticationMapper.selectPermissionByUsername(user.getName());
        RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(this.key, user.getName(),
                authorities);
        auth.setDetails(user);
        return auth;
    }

    protected boolean isTokenExpired(long tokenExpiryTime) {
        return tokenExpiryTime <= System.currentTimeMillis();
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        cancelHeader(request, response);
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        if (!rememberMeRequested(request, DEFAULT_PARAMETER)) {
            this.logger.debug("Remember-me login not requested.");
            return;
        }
        onLoginSuccess(request, response, successfulAuthentication);
    }

    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
                               Authentication successfulAuthentication) {
        String username = (String) successfulAuthentication.getPrincipal();
        // If unable to find a username and token, just abort as
        // TokenBasedRememberMeServices is
        // unable to construct a valid token in this case.
        if (StringUtils.isEmpty(username)) {
            this.logger.debug("Unable to retrieve username");
            return;
        }
        String token = generateTokenData();
        long expiryTime = calculateTokenExpiryTime();
        String signatureValue = new RememberMeSignatureHelper(this.key, ENCODING_ALGORITHM.getDigestAlgorithm())
                .withUsername(username)
                .withToken(token)
                .withDeadline(expiryTime)
                .makeTokenSignature();

        RememberMeToken persistentToken = new RememberMeToken(username, generateSeriesData(),
                token, new Date(expiryTime), signatureValue);
        authenticationMapper.saveToken(persistentToken);

        addHeader(persistentToken, request, response);
        if (this.logger.isDebugEnabled()) {
            this.logger
                    .debug("Added remember-me cookie for user '" + username + "', expiry: '" + new Date(expiryTime) + "'");
        }
    }

    private static long calculateTokenExpiryTime() {
        int tokenLifetime = FIFTEEN_DAYS_IN_SECOND;
        long expiryTime = System.currentTimeMillis();
        // SEC-949
        expiryTime += 1000L * tokenLifetime;
        return expiryTime;
    }

    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        if (this.alwaysRemember) {
            return true;
        }
        String paramValue = request.getParameter(parameter);
        if (paramValue != null) {
            if (paramValue.equalsIgnoreCase("yes")) {
                return true;
            }
        }
        this.logger.debug("Did not send remember-me cookie (principal did not set parameter '{}')", parameter);
        return false;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        cancelHeader(request, response);
    }
}
