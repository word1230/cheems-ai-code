package com.cheems.cheemsaicode.ai.core.handler;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.cheems.cheemsaicode.ai.core.message.AiResponseMessage;
import com.cheems.cheemsaicode.ai.core.message.StreamMessage;
import com.cheems.cheemsaicode.ai.core.message.StreamMessageTypeEnum;
import com.cheems.cheemsaicode.ai.core.message.ToolExecutedMessage;
import com.cheems.cheemsaicode.ai.core.message.ToolRequestMessage;
import com.cheems.cheemsaicode.model.entity.User;
import com.cheems.cheemsaicode.model.enums.MessageTypeEnum;
import com.cheems.cheemsaicode.service.ChatHistoryService;
import com.google.gson.JsonObject;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class JsonMessageStreamHandler {

    public Flux<String> handler(Flux<String> originFlux,ChatHistoryService chatHistoryService,User loginUser,Long appId){
        StringBuilder chatHistoryStringBuilder = new StringBuilder();

        Set<String> seenToolIds  = new HashSet<String>();;
        // 解析每个消息块
        return originFlux.map(chunk->{
           return  handleJsonMessageChunk(chunk, chatHistoryStringBuilder, seenToolIds );
        })
        .filter(StrUtil::isNotEmpty)
        .doOnComplete(() -> {
            // 流式响应结束后，添加完整记录到对话历史中
            chatHistoryService.saveChatHistory( appId,loginUser.getId(),chatHistoryStringBuilder.toString(),MessageTypeEnum.AI);
        })
        .doOnError((error) -> {
            // 流式响应失败后，记录错误到对话历史中
            String errorMessage = "AI响应失败："+error.getMessage();
            chatHistoryService.saveChatHistory( appId,loginUser.getId(),errorMessage,MessageTypeEnum.AI);
        });
    }
        

    private String handleJsonMessageChunk(String messageChunk,StringBuilder chathStringBuilder,Set<String> seenToolIds){
        // 解析 JSON 消息块 获取消息类型
        StreamMessage streamMessage = JSONUtil.toBean(messageChunk, StreamMessage.class);
        String messageType = streamMessage.getType();
        StreamMessageTypeEnum messageTypeEnum = StreamMessageTypeEnum.getEnumByValue(messageType);;
        // 如果是AI响应，拼接
        switch(messageTypeEnum){
            case AI_RESPONSE ->{
                AiResponseMessage aiResponseMessage = JSONUtil.toBean(messageChunk, AiResponseMessage.class);
                String data = aiResponseMessage.getData();;
                chathStringBuilder.append(data);
                return data;
            }
            case TOOL_REQUEST ->{
                       //如果是工具请求, 判断是否在set中，如果是第一次，则响应完整信息，否则为空
                ToolRequestMessage toolRequestMessage = JSONUtil.toBean(messageChunk, ToolRequestMessage.class);
                if(toolRequestMessage.getId() != null && !seenToolIds.contains(toolRequestMessage.getId())){
                    seenToolIds.add(toolRequestMessage.getId());
                    return "\n\n[选择工具] 写入文件\n\n";
                }else{
                    return "";
                }
                
            }
            case TOOL_EXECUTED ->{
        // 如果是工具执行结果，响应完整信息， 输出前端和要持久化的信息。
                ToolExecutedMessage toolExecutedMessage = JSONUtil.toBean(messageChunk, ToolExecutedMessage.class);
                JSONObject jsonObject =  JSONUtil.parseObj(toolExecutedMessage.getArguments());
                String relativeFilePath = jsonObject.getStr("relativeFilePath");
                String fileSuffix = FileUtil.getSuffix(relativeFilePath);
                String content = jsonObject.getStr("content");
                String result = String.format("""
                        [工具调用]写入文件 %s
                        ```%s
                        %s
                        ```
                        """, relativeFilePath, fileSuffix, content);
                String output = String.format("\n\n%s\n\n", result);
                chathStringBuilder.append(output);
                return output;
            }
            default ->{
                log.error("不支持的消息类型：{}", messageType);
                return "";
            }
        }
    }
}
