package com.evan.example.web.core.security;

import com.evan.example.web.system.user.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class KaltAuthentication extends AbstractAuthenticationToken {
    /**
     * 验证码
     */
    private String kalt;
    /**
     * 加密的盐值
     */
    private String salt;

    private final String principal;

    private String credentials;

    public String getKalt() {
        return kalt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public KaltAuthentication(String principal, String credentials, String kalt) {
        this(principal, credentials);
        this.kalt = kalt;
    }
    public KaltAuthentication(String principal, String credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public KaltAuthentication(String principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return this.credentials;
    }

    @Override
    public User getDetails() {
        return (User) super.getDetails();
    }

    @Override
    public String getPrincipal() {
        return this.principal;
    }
}
