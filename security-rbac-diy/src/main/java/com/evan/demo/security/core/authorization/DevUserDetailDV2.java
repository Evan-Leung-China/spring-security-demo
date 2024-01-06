package com.evan.demo.security.core.authorization;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.evan.demo.security.system.dao.DevUserRoleRepositories;
import com.evan.demo.security.system.pojo.entity.DevMenu;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.*;
import java.util.function.Function;

public class DevUserDetailDV2 implements UserDetails, CredentialsContainer {

    @Serial
    private static final long serialVersionUID = 4739291017790867318L;
    private final Integer userId;
    private String password;

    private final String username;
    /**
     * 既然我们决定定制AuthorizationManger，那就怎么方便怎么实现。
     * 决定将用户拥有的角色实体对象都存起来
     * 便于我们读取roleId-表关联，roleCode-用于权限控制
     */
    private Set<? extends GrantedAuthority> authorities;

    private final boolean accountNonExpired;

    private final boolean accountNonLocked;

    private final boolean credentialsNonExpired;

    private final boolean enabled;


    public DevUserDetailDV2(Integer userId, String username, String password, boolean enabled, boolean accountNonExpired,
                            boolean credentialsNonExpired, boolean accountNonLocked) {
        Assert.isTrue(username != null && !username.isEmpty() && password != null && userId != null,
                "Cannot pass null or empty values to constructor");
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
    }

    public Integer getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }


    /**
     * Returns {@code true} if the supplied object is a {@code User} instance with the
     * same {@code username} value.
     * <p>
     * In other words, the objects are equal if they have the same username, representing
     * the same principal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DevUserDetailDV2) {
            return this.userId.equals(((DevUserDetailDV2) obj).userId)
                    && this.username.equals(((DevUserDetailDV2) obj).username);
        }
        return false;
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, userId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [");
        sb.append("UserId=").append(this.userId).append(", ");
        sb.append("Username=").append(this.username).append(", ");
        sb.append("Password=[PROTECTED], ");
        sb.append("Enabled=").append(this.enabled).append(", ");
        sb.append("AccountNonExpired=").append(this.accountNonExpired).append(", ");
        sb.append("credentialsNonExpired=").append(this.credentialsNonExpired).append(", ");
        sb.append("AccountNonLocked=").append(this.accountNonLocked).append(", ");
        sb.append("Granted Authorities=").append(this.authorities).append("]");
        return sb.toString();
    }

    /**
     * Creates a UserBuilder
     * @return the UserBuilder
     */
    public static UserBuilder builder(Integer userId) {
        return new UserBuilder(userId);
    }

    public synchronized void loadAuthorities() {
        if (CollectionUtil.isNotEmpty(this.authorities)) {
            return;
        }

        DevUserRoleRepositories userRoleRepositories = SpringUtil.getBean(DevUserRoleRepositories.class);
        List<DevMenu> grantedMenu = userRoleRepositories.findMenuByUserId(this.getUserId());
        List<MenuGrantedAuthority> roleIdList = Collections.emptyList();
        if (CollectionUtil.isNotEmpty(grantedMenu)) {
            roleIdList = grantedMenu.stream()
                    .map(menu -> new MenuGrantedAuthority(menu.getId()))
                    .toList();
        }
        this.authorities = sortAuthorities(roleIdList);
    }

    private static SortedSet<? extends GrantedAuthority> sortAuthorities(Collection<MenuGrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<MenuGrantedAuthority> sortedAuthorities = new TreeSet<>(new Comparator<MenuGrantedAuthority>() {
            @Override
            public int compare(MenuGrantedAuthority o1, MenuGrantedAuthority o2) {
                return o2.getMenuId() - o1.getMenuId();
            }
        });
        for (MenuGrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    /**
     * Builds the user to be added. At minimum the username, password, and authorities
     * should provided. The remaining attributes have reasonable defaults.
     */
    public static final class UserBuilder {
        private String username;

        private String password;

        private boolean accountExpired;

        private boolean accountLocked;

        private boolean credentialsExpired;

        private boolean disabled;

        private Function<String, String> passwordEncoder = (password) -> password;
        private final Integer userId;
        private List<MenuGrantedAuthority> authorities;

        /**
         * Creates a new instance
         */
        private UserBuilder(Integer userId) {
            this.userId = userId;
        }

        /**
         * Populates the username. This attribute is required.
         * @param username the username. Cannot be null.
         * @return the {@link UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder username(String username) {
            Assert.notNull(username, "username cannot be null");
            this.username = username;
            return this;
        }

        public UserBuilder authorities(List<MenuGrantedAuthority> authorities) {
            Assert.notNull(authorities, "authorities cannot be null");
            this.authorities  = authorities;
            return this;
        }


        /**
         * Populates the password. This attribute is required.
         * @param password the password. Cannot be null.
         * @return the {@link UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder password(String password) {
            Assert.notNull(password, "password cannot be null");
            this.password = password;
            return this;
        }

        /**
         * Encodes the current password (if non-null) and any future passwords supplied to
         * {@link #password(String)}.
         * @param encoder the encoder to use
         * @return the {@link UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder passwordEncoder(Function<String, String> encoder) {
            Assert.notNull(encoder, "encoder cannot be null");
            this.passwordEncoder = encoder;
            return this;
        }

        /**
         * Defines if the account is expired or not. Default is false.
         * @param accountExpired true if the account is expired, false otherwise
         * @return the {@link UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder accountExpired(boolean accountExpired) {
            this.accountExpired = accountExpired;
            return this;
        }

        /**
         * Defines if the account is locked or not. Default is false.
         * @param accountLocked true if the account is locked, false otherwise
         * @return the {@link UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        /**
         * Defines if the credentials are expired or not. Default is false.
         * @param credentialsExpired true if the credentials are expired, false otherwise
         * @return the {@link UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder credentialsExpired(boolean credentialsExpired) {
            this.credentialsExpired = credentialsExpired;
            return this;
        }

        /**
         * Defines if the account is disabled or not. Default is false.
         * @param disabled true if the account is disabled, false otherwise
         * @return the {@link UserBuilder} for method chaining (i.e. to populate
         * additional attributes for this user)
         */
        public UserBuilder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public UserDetails build() {
            String encodedPassword = this.passwordEncoder.apply(this.password);
            return new DevUserDetailDV2(this.userId, this.username, encodedPassword, !
                    this.disabled, !this.accountExpired,
                    !this.credentialsExpired, !this.accountLocked);
        }

    }
}
