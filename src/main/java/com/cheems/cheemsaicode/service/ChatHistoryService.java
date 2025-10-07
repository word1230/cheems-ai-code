package com.cheems.cheemsaicode.service;

import com.cheems.cheemsaicode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.cheems.cheemsaicode.model.entity.ChatHistory;
import com.cheems.cheemsaicode.model.enums.MessageTypeEnum;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

/**
 * 对话历史 服务层。
 *
 * @author cheems
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 保存对话历史
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param message 消息内容
     * @param messageType 消息类型
     */
    void saveChatHistory(Long appId, Long userId, String message, MessageTypeEnum messageType);

    /**
     * 判断用户是否首次访问应用（即是否有历史对话记录）
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @return true-首次访问，false-已有历史记录
     */
    boolean isFirstVisit(Long appId, Long userId);

    /**
     * 构造查询条件
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);


    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory messageWindowChatMemory,int maxCount);
}
