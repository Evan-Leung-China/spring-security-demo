package com.evan.demo.security.core.authentication;

import com.evan.demo.security.core.BusinessException;
import com.evan.demo.security.system.dao.DevRoleMenuRepositories;
import com.evan.demo.security.system.pojo.vo.UrlRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UrlAuthorizationService {

    @Autowired
    private DevRoleMenuRepositories devRoleMenuRepositories;

    /**
     * 从数据库中封装接口权限
     *
     * @return 权限map
     */
    public Map<String, List<String>> buildUrlAuthorizationMap() {
        List<UrlRoleVO> urlRoleList = devRoleMenuRepositories.queryAllUriRole();
        if (CollectionUtils.isEmpty(urlRoleList)) {
            throw new BusinessException("system authorization config error.");
        }

        Map<String, List<String>> result = new HashMap<>(urlRoleList.size());
        for (UrlRoleVO urlRole : urlRoleList) {
            List<String> uriAuthRoles = result.computeIfAbsent(urlRole.menuUri(), uri -> new ArrayList<>());
            if (uriAuthRoles.contains(urlRole.roleCode())) {
                continue;
            }
            uriAuthRoles.add(urlRole.roleCode());
        }
        return result;
    }
    /**
     * 从数据库中封装接口权限
     *
     * @return 权限map
     */
    public Map<String, List<Integer>> buildUrlAuthorizationMapV2() {
        List<UrlRoleVO> urlRoleList = devRoleMenuRepositories.queryAllUriRole();
        if (CollectionUtils.isEmpty(urlRoleList)) {
            throw new BusinessException("system authorization config error.");
        }

        Map<String, List<Integer>> result = new HashMap<>(urlRoleList.size());
        for (UrlRoleVO urlRole : urlRoleList) {
            List<Integer> uriAuthRoles = result.computeIfAbsent(urlRole.menuUri(), uri -> new ArrayList<>());
            if (uriAuthRoles.contains(urlRole.roleId())) {
                continue;
            }
            uriAuthRoles.add(urlRole.roleId());
        }
        return result;
    }
}
