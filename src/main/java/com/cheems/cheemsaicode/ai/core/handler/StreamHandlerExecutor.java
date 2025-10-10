package com.cheems.cheemsaicode.ai.core.handler;

import org.springframework.stereotype.Component;

import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;
import com.cheems.cheemsaicode.model.entity.User;
import com.cheems.cheemsaicode.service.ChatHistoryService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class StreamHandlerExecutor {

    @Resource
    private JsonMessageStreamHandler jsonMessageStreamHandler;

    public Flux<String> doExecute(Flux<String> messageFlux,ChatHistoryService chatHistoryService,Long appId,User loginUser,CodeGenTypeEnum codeGenTypeEnum) {
        
      return  switch (codeGenTypeEnum) {
            case HTML,MULTI_FILE -> 
                new SimpleTextStreamHandler().handler(messageFlux,chatHistoryService,loginUser,appId);
            case VUE_PROJECT -> 
                jsonMessageStreamHandler.handler(messageFlux, chatHistoryService, loginUser, appId);
            
        }
    }
}
