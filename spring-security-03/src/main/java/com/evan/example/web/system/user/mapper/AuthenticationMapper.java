package com.evan.example.web.system.user.mapper;

import com.evan.example.web.system.authentication.RememberMeToken;
import com.evan.example.web.system.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthenticationMapper {

    public static final int EXPIRE_IN_DAYS = 15;

    private final Map<String, RememberMeToken> rememberMeRepository = new ConcurrentHashMap<>();

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

    public RememberMeToken getTokenForSeries(String presentedSeries) {
        return rememberMeRepository.get(presentedSeries);
    }

    public void saveToken(RememberMeToken token) {
        rememberMeRepository.put(token.getSeries(), token);
    }

    public void removeUserTokens(String username) {

    }

    public void updateToken(String series, RememberMeToken token) {
        RememberMeToken oldToken = rememberMeRepository.remove(series);
        rememberMeRepository.put(series, token);
    }
}
