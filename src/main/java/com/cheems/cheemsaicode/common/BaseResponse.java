package com.cheems.cheemsaicode.common;

import com.cheems.cheemsaicode.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 响应结果封装类
 * @param <T>
 */
@Data
public class BaseResponse <T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private int code;
    private T data;
    private String msg;


    public BaseResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }
    public BaseResponse(int code, T data) {
        this(code, data, null);
    }
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

}
