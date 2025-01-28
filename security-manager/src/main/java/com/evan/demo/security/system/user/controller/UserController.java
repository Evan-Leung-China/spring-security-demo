/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evan.demo.security.system.user.controller;

import com.evan.demo.security.system.authorization.annotation.HasPermission;
import com.evan.demo.security.system.user.pojo.dto.UserDTO;
import com.evan.demo.security.system.user.pojo.entity.DevUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@Slf4j
@Controller
public class UserController {

    // http://127.0.0.1:8080/hello?name=lisi
    @RequestMapping("/hello")
    @ResponseBody
    @HasPermission("system:user:query")
    public String hello(@SessionAttribute("hairCutMaster") String hairCutMaster, @RequestParam(name = "name", defaultValue = "unknown user") String name) {
        log.info("hairCutMaster:{}", hairCutMaster);
        return "Hello " + name;
    }

    // http://127.0.0.1:8080/user
    @RequestMapping("/user")
    @ResponseBody
    @HasPermission("system:user:edit")
    public DevUser user() {
        DevUser user = new DevUser();
        return user;
    }

    // http://127.0.0.1:8080/save_user?name=newName&age=11
    @RequestMapping("/save_user")
    @ResponseBody
    @HasPermission("system:user:add")
    public String saveUser(UserDTO u) {
        return "user will save: name=" + u.getUserName();
    }

    // http://127.0.0.1:8080/html
    @RequestMapping("/html")
    public String html(){
        return "index.html";
    }
}
