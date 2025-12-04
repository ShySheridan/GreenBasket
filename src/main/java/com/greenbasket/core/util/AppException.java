package com.greenbasket.core.util;

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