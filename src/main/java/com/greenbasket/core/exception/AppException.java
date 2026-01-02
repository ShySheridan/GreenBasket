package com.greenbasket.core.exception;

enum ErrorCode {
    INVALID_QUANTITY,
    OUT_OF_STOCK,
    CATEGORY_NOT_EMPTY
}

public class AppException extends RuntimeException {
    private final String code;

    public AppException(String code) {
        super(code);
        this.code = code;
    }
    public String code() {
        return code;
    }
}