package com.evan.demo.security.system.user;

import com.evan.demo.security.system.user.dao.DevRoleMenuRepository;
import com.evan.demo.security.system.user.pojo.entity.DevMenu;
import com.evan.demo.security.system.user.pojo.entity.DevRoleMenu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AuthorityService {

    @Autowired
    private DevRoleMenuRepository devRoleMenuRepository;

    public List<GrantedAuthority> selectByRoleIds(List<Integer> roleIds) {
        Iterable<DevMenu> roleMenus = devRoleMenuRepository.findAllMenuByRoleIds(roleIds);
        if (roleIds.size() == 0) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> authorities = new ArrayList<>(20);
        for (DevMenu roleMenu : roleMenus) {
            authorities.add(new SimpleGrantedAuthority(roleMenu.getPermission()));
        }
        return authorities;
    }

}
