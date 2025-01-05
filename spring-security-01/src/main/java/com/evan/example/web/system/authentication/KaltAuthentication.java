package com.evan.example.web.system.authentication;

import com.evan.example.web.system.user.model.User;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
public class KaltAuthentication implements Authentication, Serializable {
    private final Collection<? extends GrantedAuthority> authorities;
    /**
     * 验证码
     */
    private final String kalt;
    /**
     * 加密的盐值
     */
    private final String salt;

    private final String principal;

    private final String credentials;

    private Boolean authenticated;

    private final User details;

    @Override
    public User getDetails() {
        return details;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;
    }

    @Override
    public String getName() {
        return this.principal;
    }
}
