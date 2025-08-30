package com.cheems.cheemsaicode.utils;

import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;

/**
 * 异常抛出工具类
 */
public class ThrowUtils {

    public static void throwIf(boolean condition, RuntimeException exception){
        if(condition){
            throw exception;
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode){
        throwIf(condition, new BusinessException(errorCode));
    }

    public static void throwIf(boolean condition, ErrorCode errorCode, String message){
        throwIf(condition, new BusinessException(errorCode, message));
    }


}
