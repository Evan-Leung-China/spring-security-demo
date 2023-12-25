package com.evan.demo.security.core.authorization;

import cn.hutool.core.collection.CollectionUtil;
import com.evan.demo.security.system.dao.DevUserRepositories;
import com.evan.demo.security.system.dao.DevUserRoleRepositories;
import com.evan.demo.security.system.pojo.entity.DevMenu;
import com.evan.demo.security.system.pojo.entity.DevUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class DevUserDetailServiceV4 implements UserDetailsService {
    private final DevUserRepositories userRepositories;
    private final DevUserRoleRepositories userRoleRepositories;

    @Autowired
    public DevUserDetailServiceV4(DevUserRepositories devUserRepositories,
                                  DevUserRoleRepositories userRoleRepositories) {
        this.userRepositories = devUserRepositories;
        this.userRoleRepositories = userRoleRepositories;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DevUser user = userRepositories.findByUserCode(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(username + " not found.");
        }

        List<DevMenu> grantedMenu = userRoleRepositories.findMenuByUserId(user.getId());
        List<MenuGrantedAuthority> roleIdList = Collections.emptyList();
        if (CollectionUtil.isNotEmpty(grantedMenu)) {
            roleIdList = grantedMenu.stream()
                    .map(menu -> new MenuGrantedAuthority(menu.getId()))
                    .toList();
        }

        return DevUserDetailV4.builder(user.getId())
                .username(user.getUserName())
                .password(user.getPassword())
                .accountLocked(user.getLock())
                .authorities(roleIdList)
                // TODO：禁用
                .disabled(false)
                // TODO: 账号期限
                .accountExpired(false)
                // 刚登录，凭证自然是有效的
                .credentialsExpired(false)
                .build();
    }
}
