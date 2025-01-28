package com.evan.demo.security.core;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    /**
     * 默认成功
     */
    SUCCESS("200000", "处理成功"),
    ERROR("400000", "系统错误"),

    AUTHENTICATION_ERROR("400100", "登录失败"),

    AUTHENTICATION_REQUIRED("400101", "请先登录再访问"),
    SESSION_EXPIRED("400110", "会话超时"),

    ACCESS_DENIED_ERROR("400120", "您无权限访问该资源");
    private final String code;
    private final String msg;

    ErrorCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
