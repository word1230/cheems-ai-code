package com.cheems.cheemsaicode.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeGeneratorServiceFactory {


    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Bean
    public AICodeGeneratorService getAiCodeGeneratorService() {
         return    AiServices.builder(AICodeGeneratorService.class)
                 .chatModel(chatModel)
                 .streamingChatModel(streamingChatModel)
                 .build();
    }


}
