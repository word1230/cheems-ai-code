package com.cheems.cheemsaicode.ai;

import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;

import dev.langchain4j.service.SystemMessage;
/**
 * AI代码生成类型智能路由服务
 * 使用结构化输出直接返回枚举类型
 * @author cheems
 */
public interface AiCodeGenTypeRoutingService {

    /**
     * 根据用户需求智能选择生成代码的类型
     * @param userPrompt
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenTypeEnum(String userPrompt);

}
