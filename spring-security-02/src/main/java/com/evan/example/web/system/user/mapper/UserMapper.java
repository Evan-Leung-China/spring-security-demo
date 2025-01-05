package com.evan.example.web.system.user.mapper;

import com.evan.example.web.system.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public User selectByUsername(String username) {
        User user = new User();
        user.setName("evan");
        user.setAge(18);
        return user;
    }

    public List<GrantedAuthority> selectPermissionByUsername(String username) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add((GrantedAuthority) () -> "system:user:query");
        authorities.add((GrantedAuthority) () -> "system:user:add");
        return authorities;
    }
}
