package com.evan.demo.security.system.user.pojo.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String userCode;
    private String userName;
    private String password;
    private Boolean lock;
}
