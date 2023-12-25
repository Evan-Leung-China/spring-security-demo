package com.evan.demo.security.core;

import java.io.Serial;

public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1430945061363985748L;

    public BusinessException(String message) {
        super(message);
    }
}
