package com.cheems.cheemsaicode.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.cheems.cheemsaicode.model.enums.MessageTypeEnum;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.cheems.cheemsaicode.model.entity.ChatHistory;
import com.cheems.cheemsaicode.mapper.ChatHistoryMapper;
import com.cheems.cheemsaicode.service.ChatHistoryService;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 对话历史 服务层实现。
 *
 * @author cheems
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

    @Override
    public void saveChatHistory(Long appId, Long userId, String message, MessageTypeEnum messageType) {

        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        ThrowUtils.throwIf(messageType == null, ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        // 验证消息类型是否有效
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .userId(userId)
                .message(message)
                .messageType(messageType.getValue())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        this.save(chatHistory);
    }

    @Override
    public boolean isFirstVisit(Long appId, Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("appId = {0}", appId)
                .and("userId = {0}", userId);
        long count = this.count(queryWrapper);
        return count == 0;
    }

    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        if (chatHistoryQueryRequest == null) {
            return null;
        }
        Long id = chatHistoryQueryRequest.getId();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        String messageType = chatHistoryQueryRequest.getMessageType();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();

        return QueryWrapper.create()
                .eq("id", id)
                .eq("appId", appId)
                .eq("userId", userId)
                .eq("messageType", messageType)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory messageWindowChatMemory,int maxCount) {

       try{
           QueryWrapper queryWrapper = QueryWrapper.create()
                   .eq(ChatHistory::getAppId, appId)
                   .orderBy(ChatHistory::getCreateTime, false)
                   .limit(1, maxCount);
           List<ChatHistory> list = this.list(queryWrapper);
           if (list == null) {
               return 0;
           }

           List<ChatHistory> historyList = list.reversed();

           int loadCount = 0;

           for (ChatHistory history : historyList) {
               if (Objects.equals(history.getMessageType(), MessageTypeEnum.USER.getValue())) {
                   messageWindowChatMemory.add(UserMessage.from(history.getMessage()));
               } else if (Objects.equals(history.getMessageType(), MessageTypeEnum.AI.getValue()))
                   messageWindowChatMemory.add(UserMessage.from(history.getMessage()));

               loadCount++;
           }
           log.info("成功为appid:{} 加载了{} 条对话", appId, loadCount);
           return loadCount;
       }catch (Exception e){
           log.info("加载对话失败：{}", e.getMessage());
           return 0;
       }

    }
}
