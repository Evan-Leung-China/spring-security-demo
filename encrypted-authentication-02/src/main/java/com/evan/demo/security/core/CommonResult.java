package com.evan.demo.security.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {
    private String code;
    private String msg;
    private T data;

    public static CommonResult<Void> buildFailure(ErrorCodeEnum error) {
        return new CommonResult<>(error.getCode(), error.getMsg(), null);
    }
    public static <T> CommonResult<T> buildFailure(ErrorCodeEnum error, Class<T> clazz) {
        return new CommonResult<>(error.getCode(), error.getMsg(), null);
    }

    public static <T> CommonResult<T> buildFailure(ErrorCodeEnum error, String message, Class<T> clazz) {
        return new CommonResult<>(error.getCode(), message, null);
    }

    public static <T> CommonResult<T> buildSuccess(T data) {
        return new CommonResult<>(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMsg(), data);
    }

}
