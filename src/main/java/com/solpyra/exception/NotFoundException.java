package com.solpyra.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends Exception {
    private final String errorCode;

    public NotFoundException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public NotFoundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}