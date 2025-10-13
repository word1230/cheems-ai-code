package com.cheems.cheemsaicode.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;

/**
 * 应用名称生成器工厂类
 * 提供应用名称生成器的创建方法
 */
@Configuration
public class AiCodeGenAppParamFactory {

    @Resource
    private ChatModel chatModel;

    @Bean
    public AiCodeGenAppParamService aiCodeGenAppParamService(){
        return AiServices.builder(AiCodeGenAppParamService.class)
                        .chatModel(chatModel)
                        .build();
    }


}
