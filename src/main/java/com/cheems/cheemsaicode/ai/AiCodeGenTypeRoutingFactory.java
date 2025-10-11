package com.cheems.cheemsaicode.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AiCodeGenTypeRoutingFactory {

    @Resource
    private ChatModel chatModel;
    @Bean
    public  AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService(){
       return  AiServices.builder(AiCodeGenTypeRoutingService.class)
            .chatModel(chatModel)
            .build();
    }
}
