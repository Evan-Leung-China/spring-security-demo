package com.evan.demo.security.core.authentication.v4;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.Serial;
import java.util.*;

/**
 * 当前这个版本，与老系统的模式相适配：授权只到菜单，接口权限跟随菜单。
 * 在之前的版本中，为了便于与请求URL进行匹配，都是通过《uri, List《roleId》》。
 * 这种方式，最糟糕的情况下，需要遍历整个Map.Entry
 * 既然，我们是基于菜单进行授权，那为了提高匹配效率。可以考虑如下方案：
 * UserDetail-存储MenuId， AuthorizationManager-存储Map《menuId, List《uri》
 * 因此，如果我们希望通过自定义GrantedAuthority，那么只需要持有一个MenuId就行了。
 * 当然，也可以像v2那样，跳过GrantedAuthority。直接在UserDetail中定义List《menuId》
 *
 * 鉴于菜单的接口权限总是固定的，因此，我们可以直接构建并进行共享。
 *
 * 当然，这种方案也有其弊端，那就是不可避免的重复存储某些共享接口：例如字典接口。
 * 不过有一点额外之喜就是，AuthorizationManager中的权限彻底与用户无关。任你如何给用户修改菜单权限，这里岿然不动。因为菜单的接口权限依然没有变。
 */
public class MenuGrantedAuthority implements GrantedAuthority {
    @Serial
    private static final long serialVersionUID = -3011502893145246026L;

    private final Integer menuId;

    private Set<AntPathRequestMatcher> menuInterfaceList;
    public MenuGrantedAuthority(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public synchronized void addAuthorityUri(String uri) {
        if (Objects.isNull(menuInterfaceList)) {
            menuInterfaceList = new HashSet<>();
        }
        menuInterfaceList.add(new AntPathRequestMatcher(uri));
    }

    public boolean containsUri(HttpServletRequest request) {
        Optional<AntPathRequestMatcher> any = this.menuInterfaceList.stream()
                .filter(grantedUri -> grantedUri.matches(request))
                .findAny();
        return any.isPresent();
    }

    @Override
    public String getAuthority() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuGrantedAuthority that = (MenuGrantedAuthority) o;
        return Objects.equals(menuId, that.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }

    @Override
    public String toString() {
        return "MenuGrantedAuthority{" +
                "menuId=" + menuId +
                ", menuInterfaceList=" + menuInterfaceList +
                '}';
    }
}
