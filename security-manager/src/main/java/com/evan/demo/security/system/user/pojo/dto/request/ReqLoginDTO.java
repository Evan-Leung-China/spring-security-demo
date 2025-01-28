package com.evan.demo.security.system.user.pojo.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReqLoginDTO implements Serializable {
    private String username;
    private String password;
    /**
     * 验证码
     */
    private String captcha;
}
