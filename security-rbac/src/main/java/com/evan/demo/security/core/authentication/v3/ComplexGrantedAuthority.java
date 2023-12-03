package com.evan.demo.security.core.authentication.v3;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Objects;

/**
 * 相较于v2版本的不再使用GrantedAuthority，spring-security推荐的做法是自定义一个GrantedAuthority
 * 并返回null，由特定的AuthorizationManager进行处理。
 *
 * 鉴于此，当前v3版本则是按照推荐做法，定义了一个持有角色ID的权限
 */
public class ComplexGrantedAuthority implements GrantedAuthority {
    @Serial
    private static final long serialVersionUID = -3011502893145246026L;

    public Integer getRoleId() {
        return roleId;
    }

    private final Integer roleId;

    public ComplexGrantedAuthority(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getAuthority() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexGrantedAuthority that = (ComplexGrantedAuthority) o;
        return Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }
}
