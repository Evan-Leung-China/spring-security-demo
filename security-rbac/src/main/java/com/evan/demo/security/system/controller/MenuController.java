package com.evan.demo.security.system.controller;

import cn.hutool.core.lang.tree.Tree;
import com.evan.demo.security.core.authentication.v1.DevUserDetailV1;
import com.evan.demo.security.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/menu")
@RestController
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("/query")
    public List<Tree<Integer>> queryAllMenu(@CurrentSecurityContext(expression = "authentication")
                                     Authentication authentication) {
        return menuService.queryAllMenu((DevUserDetailV1) authentication.getPrincipal());
    }

}
