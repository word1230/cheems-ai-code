package com.cheems.cheemsaicode.ai.core.parser;

import com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;

public class CodeParserExecutor {

    public  static  HtmlCodeParser htmlCodeParser = new HtmlCodeParser();
    public  static  MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    public static Object parseCode(String codeContent, AIGenTypeEnum genType) {
        switch (genType){
            case HTML:
                return htmlCodeParser.parseCode(codeContent);
            case MULTI_FILE:
                return multiFileCodeParser.parseCode(codeContent);
            default:
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成模式"+genType);
        }
    }
}
