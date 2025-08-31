package com.cheems.cheemsaicode.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeGeneratorServiceFactory {


    @Resource
    private ChatModel chatModel;

    @Bean
    public AICodeGeneratorService getAiCodeGeneratorService() {
         return    AiServices.create(AICodeGeneratorService.class,chatModel);
    }


}
