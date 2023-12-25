package com.evan.demo.security.system.pojo.vo;

import lombok.Data;

@Data
public class UserVO {

    private Integer id;

    private String userCode;

    private String userName;

    private String password;

    private Boolean lock;

}
