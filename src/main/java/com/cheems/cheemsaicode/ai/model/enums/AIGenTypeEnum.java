package com.cheems.cheemsaicode.ai.model.enums;

import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import lombok.Getter;

/**
 * AI生成模式枚举
 */
@Getter
public enum AIGenTypeEnum {

    HTML("原生HTML模式","html"),
    MULTI_FILE("原生多文件模式","multi_file");




    private String text;
    private String value;

     AIGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public AIGenTypeEnum getEnumByValue(String value) {
        ThrowUtils.throwIf(value == null , ErrorCode.PARAMS_ERROR);
        for(AIGenTypeEnum e : AIGenTypeEnum.values()) {
            if(e.value.equals(value)) {
                return e;
            }
        }
        return null;

    }


}
