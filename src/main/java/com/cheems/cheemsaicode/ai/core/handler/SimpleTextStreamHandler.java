package com.cheems.cheemsaicode.ai.core.handler;
import com.cheems.cheemsaicode.ai.core.message.StreamMessageTypeEnum;
import com.cheems.cheemsaicode.model.entity.User;
import com.cheems.cheemsaicode.model.enums.MessageTypeEnum;
import com.cheems.cheemsaicode.service.ChatHistoryService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class SimpleTextStreamHandler {


    /**
     * 处理传统流（Html，Multi_File）
     * 直接收集响应的数据
     * @param originFlux
     * @param chatHistoryService
     * @param loginUser
     * @param appId
     * @return
     */
    public Flux<String> handler(Flux<String> originFlux,ChatHistoryService chatHistoryService,User loginUser,Long appId){
        StringBuilder aiResponseBuilder = new StringBuilder();
        return originFlux.map((chunk)->{
            aiResponseBuilder.append(chunk);
            return chunk;
        }).doOnComplete(() -> {
            String aiResponse = aiResponseBuilder.toString();;
            log.info("AI响应:{}", aiResponse);
            chatHistoryService.saveChatHistory(appId,loginUser.getId(),aiResponse, MessageTypeEnum.AI);
        })
        .doOnError(error ->{
            String errorMessage = "AI回复失败"+error.getMessage();
            log.error(errorMessage,error);
            chatHistoryService.saveChatHistory(appId,loginUser.getId(),errorMessage, MessageTypeEnum.AI);
        });

    }

}
