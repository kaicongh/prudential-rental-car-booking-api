package com.prudential.rcb.data.exception;

public class BusinessCheckException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessCheckException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
