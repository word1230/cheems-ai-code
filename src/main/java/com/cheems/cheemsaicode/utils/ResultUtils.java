package com.cheems.cheemsaicode.utils;

import com.cheems.cheemsaicode.common.BaseResponse;
import com.cheems.cheemsaicode.exception.ErrorCode;

/**
 * 结果工具类
 * @author Cheems
 */
public class ResultUtils {


    public static <T>BaseResponse<T> success(T data) {
        return new BaseResponse<T>(ErrorCode.SUCCESS.getCode(), data,"ok");
    }

    public static <T>BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<T>(errorCode.getCode(), null, errorCode.getMessage());
    }

    public static <T>BaseResponse<T> error(ErrorCode errorCode, String message) {
        return new BaseResponse<T>(errorCode.getCode(), null, message);
    }
    public static <T>BaseResponse<T> error(int code,String message ) {
        return new BaseResponse<T>(code, null, message);
    }


}
