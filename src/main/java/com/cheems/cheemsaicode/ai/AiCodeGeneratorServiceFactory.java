package com.cheems.cheemsaicode.ai;

import com.cheems.cheemsaicode.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class AiCodeGeneratorServiceFactory {


    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;


    @Resource
    private ChatHistoryService chatHistoryService;

    private final Cache<Long,AICodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30L))
            .expireAfterAccess(Duration.ofMinutes(10L))
            .removalListener((key, value, cause) -> {
                log.debug("AI服务实例被移除,appid: {},原因 :{}", key, cause);
            })
            .build();


    public AICodeGeneratorService createAiCodeGeneratorService(Long appId) {
        log.info("为appId:{} 创建新的服务实例",appId);
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory,20);
         return    AiServices.builder(AICodeGeneratorService.class)
                 .chatModel(chatModel)
                 .streamingChatModel(streamingChatModel)
                 .chatMemory(chatMemory)
                 .build();
    }

    public AICodeGeneratorService getAiCodeGeneratorService(Long appId) {
        return serviceCache.get(appId, this::createAiCodeGeneratorService);
    }

    @Bean
    public AICodeGeneratorService getAiCodeGeneratorService() {
        return createAiCodeGeneratorService(0L);
    }

}
