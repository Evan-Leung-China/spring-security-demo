package com.evan.demo.security.core.authentication.v2;

import cn.hutool.core.collection.CollectionUtil;
import com.evan.demo.security.system.dao.DevRoleRepositories;
import com.evan.demo.security.system.dao.DevUserRepositories;
import com.evan.demo.security.system.dao.DevUserRoleRepositories;
import com.evan.demo.security.system.pojo.entity.DevUser;
import com.evan.demo.security.system.pojo.entity.DevUserRole;
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
@ConditionalOnProperty(prefix = "dev-security", name = "version", havingValue = "v2")
public class DevUserDetailServiceV2 implements UserDetailsService {
    private final DevUserRepositories userRepositories;
    private final DevUserRoleRepositories userRoleRepositories;
    private final DevRoleRepositories roleRepositories;

    @Autowired
    public DevUserDetailServiceV2(DevUserRepositories devUserRepositories,
                                  DevUserRoleRepositories userRoleRepositories,
                                  DevRoleRepositories roleRepositories) {
        this.userRepositories = devUserRepositories;
        this.userRoleRepositories = userRoleRepositories;
        this.roleRepositories = roleRepositories;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DevUser user = userRepositories.findByUserCode(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(username + " not found.");
        }

        List<DevUserRole> userRoleList = userRoleRepositories.findByUserId(user.getId());
        List<Integer> roleIdList = Collections.emptyList();
        if (CollectionUtil.isNotEmpty(userRoleList)) {
            roleIdList = userRoleList.stream().map(DevUserRole::getRoleId).toList();
        }

        return DevUserDetailV2.builder(user.getId())
                .username(user.getUserName())
                .password(user.getPassword())
                .accountLocked(user.getLock())
                .roleIdList(roleIdList)
                // TODO：禁用
                .disabled(false)
                // TODO: 账号期限
                .accountExpired(false)
                // 刚登录，凭证自然是有效的
                .credentialsExpired(false)
                .build();
    }
}
