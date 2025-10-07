package com.cheems.cheemsaicode.ai.model.enums;

import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import lombok.Getter;

/**
 * AI生成模式枚举
 */
@Getter
public enum CodeGenTypeEnum {

    HTML("原生HTML模式","html"),
    MULTI_FILE("原生多文件模式","multi_file");




    private String text;
    private String value;

     CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static CodeGenTypeEnum getEnumByValue(String value) {
        ThrowUtils.throwIf(value == null , ErrorCode.PARAMS_ERROR);
        for(CodeGenTypeEnum e : CodeGenTypeEnum.values()) {
            if(e.value.equals(value)) {
                return e;
            }
        }
        return null;

    }


}
