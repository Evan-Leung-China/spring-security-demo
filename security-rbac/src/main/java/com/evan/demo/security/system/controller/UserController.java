package com.evan.demo.security.system.controller;

import com.evan.demo.security.system.dao.DevUserRepositories;
import com.evan.demo.security.system.pojo.converter.UserConverter;
import com.evan.demo.security.system.pojo.entity.DevUser;
import com.evan.demo.security.system.pojo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private DevUserRepositories devUserRepositories;

    @GetMapping("/query/{id}")
    public UserVO queryUser(@PathVariable("id") Integer id) {
        Optional<DevUser> devUser = devUserRepositories.findById(id);
        if (devUser.isEmpty()) {
            return null;
        }
        return UserConverter.CONVERTER.toTto(devUser.get());
    }
}
