package com.evan.demo.security.core.authorization;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.evan.demo.security.system.dao.DevMenuInterfaceRepositories;
import com.evan.demo.security.system.pojo.entity.DevMenuInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MenuGrantedAuthorityManager {

    @Autowired
    private DevMenuInterfaceRepositories menuInterfaceRepositories;

    public MenuGrantedAuthority getGrantedAuthority(Integer menuId) {
        return ManuAuthorityHolder.manuAuthorityMap.get(menuId);
    }


    public class ManuAuthorityHolder {
        static Map<Integer, MenuGrantedAuthority> manuAuthorityMap;

        static {
            DevMenuInterfaceRepositories interfaceRepositories = SpringUtil.getBean(DevMenuInterfaceRepositories.class);
            Iterable<DevMenuInterface> all = interfaceRepositories.findAll();
            if (CollectionUtil.isEmpty(all)) {
                manuAuthorityMap = Collections.emptyMap();
            }

            Iterator<DevMenuInterface> iterator = all.iterator();
            Map<Integer, MenuGrantedAuthority> manuAuthorityMap = new HashMap<>();
            while (iterator.hasNext()) {
                DevMenuInterface next = iterator.next();
                manuAuthorityMap.computeIfAbsent(next.getMenuId(), MenuGrantedAuthority::new)
                        .addAuthorityUri(next.getUri());
            }
            // 注意：这里构建的是不允许修改的Map，意味这如果有开发同学搞乌龙，没有充分授权
            // 将无法通过SQL脚本修改这里，也无法通过后管接口修改。只能重新启动应用。
            // 因为这个地方本身就是菜单调哪些接口就是明确的。——希望各位同学养成良好的习惯。
            // BTW，调整用户权限时，我们调整的是给用户授权的菜单。也不需要调整菜单所绑定的接口
            ManuAuthorityHolder.manuAuthorityMap = Collections.unmodifiableMap(manuAuthorityMap);
        }
    }
}
