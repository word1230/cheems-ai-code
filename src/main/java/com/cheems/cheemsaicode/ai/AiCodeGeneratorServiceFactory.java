package com.cheems.cheemsaicode.ai;

import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;
import com.cheems.cheemsaicode.ai.tools.FileWriteTool;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class AiCodeGeneratorServiceFactory {


    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel openAiStreamingChatModel;


    @Resource
    private  StreamingChatModel reasoningStreamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;


    @Resource
    private ChatHistoryService chatHistoryService;

    private final Cache<String,AICodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI服务实例被移除,appid: {},原因 :{}", key, cause);
            })
            .build();


    public AICodeGeneratorService createAiCodeGeneratorService(Long appId, CodeGenTypeEnum genTypeEnum) {
        log.info("为appId:{} 创建新的服务实例",appId);
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory,20);
         return    switch (genTypeEnum){
             case VUE_PROJECT -> AiServices.builder(AICodeGeneratorService.class)
                     .streamingChatModel(reasoningStreamingChatModel)
                     .chatMemoryProvider(memoryId -> chatMemory )
                     .tools(new FileWriteTool())
                     .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                             toolExecutionRequest,"Error: there is no tool called" + toolExecutionRequest.name()
                     ))
                     .build();
             case HTML,MULTI_FILE -> AiServices.builder(AICodeGeneratorService.class)
                     .chatModel(chatModel)
                     .streamingChatModel(openAiStreamingChatModel)
                     .chatMemory(chatMemory)
                     .build();
             default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持的代码生成类型:"+ genTypeEnum.getValue());
         };
    }

    public AICodeGeneratorService getAiCodeGeneratorService(Long appId,CodeGenTypeEnum genTypeEnum) {
        String cacheKey = buildCacheKey(appId, genTypeEnum);
        return serviceCache.get(cacheKey, key ->createAiCodeGeneratorService(appId, genTypeEnum));
    }

    public AICodeGeneratorService getAiCodeGeneratorService(Long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }

    private String buildCacheKey(Long appId, CodeGenTypeEnum genTypeEnum) {
        return appId + "_" + genTypeEnum.getValue() ;
    }

}
