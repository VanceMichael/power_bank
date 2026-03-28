package com.powerbank.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    // 常用错误码
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int INTERNAL_ERROR = 500;
    
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = BAD_REQUEST;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public static BusinessException badRequest(String message) {
        return new BusinessException(BAD_REQUEST, message);
    }
    
    public static BusinessException unauthorized(String message) {
        return new BusinessException(UNAUTHORIZED, message);
    }
    
    public static BusinessException forbidden(String message) {
        return new BusinessException(FORBIDDEN, message);
    }
    
    public static BusinessException notFound(String message) {
        return new BusinessException(NOT_FOUND, message);
    }
}
