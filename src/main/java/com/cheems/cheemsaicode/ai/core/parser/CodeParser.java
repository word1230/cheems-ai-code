package com.cheems.cheemsaicode.ai.core.parser;

public interface CodeParser<T> {

    /**
     * 解析代码
     * @param codeContent
     * @return
     */
    T parseCode(String codeContent);

}
