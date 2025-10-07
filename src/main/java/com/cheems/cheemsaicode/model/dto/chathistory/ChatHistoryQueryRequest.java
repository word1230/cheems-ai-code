package com.cheems.cheemsaicode.model.dto.chathistory;

import com.cheems.cheemsaicode.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询对话历史请求
 *
 * @author cheems
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatHistoryQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 消息类型 (user/ai)
     */
    private String messageType;
}
