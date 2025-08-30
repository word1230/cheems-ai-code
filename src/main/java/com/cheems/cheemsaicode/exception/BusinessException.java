package com.cheems.cheemsaicode.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(final Integer code, final String message) {
        super(message);
        this.code = code;
    }
    public BusinessException(ErrorCode errorCode){
        this(errorCode.getCode(), errorCode.getMessage());
    }
    public BusinessException(ErrorCode errorCode, String message){
        this(errorCode.getCode(), message);
    }
}
