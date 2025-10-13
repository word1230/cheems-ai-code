package com.cheems.cheemsaicode.ai;

import com.cheems.cheemsaicode.ai.model.AiCodeGenAppParam;

import dev.langchain4j.service.SystemMessage;

public interface AiCodeGenAppParamService {

    /**
     * 根据用户的输入，生成应用名称
     */
    @SystemMessage(fromResource = "prompt/codegen-param-system-prompt.txt")
    AiCodeGenAppParam generateCodeGenAppParam(String userPrompt);

}
