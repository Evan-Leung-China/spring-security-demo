package com.evan.example.web.core.security;

import com.alibaba.fastjson.JSON;
import com.evan.example.web.system.user.dto.request.ReqLoginDTO;
import com.evan.example.web.system.user.mapper.UserMapper;
import com.evan.example.web.system.user.model.User;
import com.evan.example.web.utils.AesUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class SecuredBodyAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "POST");
    private static final AuthenticationManager NO_OPS = authentication -> {
        throw new AuthenticationServiceException("Cannot authenticate " + authentication);
    };

    protected SecuredBodyAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, NO_OPS);
    }

    private String aesKey;

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(filterProcessesUrl, HttpMethod.POST.name());
        super.setRequiresAuthenticationRequestMatcher(requestMatcher);
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        Assert.notNull(this.aesKey, "aesKey must be specified");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        byte[] bytes = IOUtils.readFully(request.getInputStream(), request.getContentLength());
        log.info("encryptedBody length: {}", bytes.length);
        ReqLoginDTO loginRequest;
        try {
            loginRequest = Optional.ofNullable(bytes)
                    .map(String::new)
                    .map(encryptedBody -> AesUtils.decrypt(encryptedBody, aesKey))
                    .filter(StringUtils::isNotEmpty)
                    .map(requestBody -> JSON.parseObject(requestBody, ReqLoginDTO.class))
                    .orElseThrow(() -> new BadCredentialsException("用户名或密码不能为空"));
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AuthenticationServiceException("解析入参错误！");
        }

        KaltAuthentication kaltAuthentication = new KaltAuthentication(loginRequest.getUsername(), loginRequest.getPassword(), loginRequest.getKalt());
        kaltAuthentication.setSalt(aesKey);
        // 如果还有其他数据需要处理，可在认证成功后进行
        return getAuthenticationManager().authenticate(kaltAuthentication);
    }
}
