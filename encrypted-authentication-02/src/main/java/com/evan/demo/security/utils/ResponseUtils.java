package com.evan.demo.security.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.evan.demo.security.core.CommonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ResponseUtils {
    public static void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
        sendResponse(request, response, "{\"code\":\"100100\", \"msg\":\"" + exception.getMessage() + "\"}");
    }

    public static <T> void sendResponse(HttpServletRequest request, HttpServletResponse response, CommonResult<T> result) throws IOException {
        if (Objects.isNull(result)) {
            sendErrorResponse(request, response, new RuntimeException("系统错误"));
            return;
        }
        ObjectMapper objectMapper = SpringUtil.getBean(ObjectMapper.class);
        String json = objectMapper.writeValueAsString(result);

        sendResponse(request, response, json);
    }

    public static void sendResponse(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        PrintWriter writer = response.getWriter();
        writer.write(message);
        writer.flush();
    }
}
