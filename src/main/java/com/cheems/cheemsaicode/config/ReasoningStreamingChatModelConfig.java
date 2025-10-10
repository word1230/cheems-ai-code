package com.cheems.cheemsaicode.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "langchain4j.openai.streaming-chat-model")
@Data
public class ReasoningStreamingChatModelConfig {

    private String apiKey;
    private String baseUrl;


    /**
     * 推理流式模型， 用于vue项目的生成，带工具调用
     */
    @Bean
    public StreamingChatModel reasoningStreamingChatModel() {


        String modelName = "ZhipuAI/GLM-4.6";

//        String modelName = "gemini-2.5-flash-maxthinking";            todo 生产使用

        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }


}
