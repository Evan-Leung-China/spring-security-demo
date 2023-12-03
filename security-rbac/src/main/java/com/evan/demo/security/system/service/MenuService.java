package com.evan.demo.security.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import com.evan.demo.security.core.authentication.v1.DevUserDetailV1;
import com.evan.demo.security.system.dao.DevRoleMenuRepositories;
import com.evan.demo.security.system.pojo.entity.DevMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private DevRoleMenuRepositories roleMenuRepositories;

    public List<Tree<Integer>> queryAllMenu(DevUserDetailV1 currentUser) {
        List<DevMenu> menuList = roleMenuRepositories.findAllMenuByRoleIdList(currentUser.getRoleIdList());
        if (CollectionUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名
        treeNodeConfig.setWeightKey("order"); // 权重排序字段 默认为weight
        treeNodeConfig.setIdKey("id"); // 默认为id可以不设置
        treeNodeConfig.setNameKey("name"); // 节点名对应名称 默认为name
        treeNodeConfig.setParentIdKey("parent"); // 父节点 默认为parentId
        treeNodeConfig.setChildrenKey("children"); // 子点 默认为children
        treeNodeConfig.setDeep(5); // 可以配置递归深度 从0开始计算 默认此配置为空,即不限制

        return TreeUtil.build(menuList, -1, new NodeParser<DevMenu, Integer>() {
            @Override
            public void parse(DevMenu menu, Tree<Integer> tree) {

                tree.setId(menu.getId());
                tree.setParentId(menu.getParent());
                // 设置权重，可改变兄弟节点的顺序
                tree.setWeight(menu.getId());
                tree.setName(menu.getMenuName());

                tree.putExtra("menuCode", menu.getMenuCode());
                tree.putExtra("menuUri", menu.getMenuUri());
                tree.putExtra("menuType", menu.getMenuType());
                tree.putExtra("menuIcon", menu.getMenuIcon());
            }
        });
    }
}
