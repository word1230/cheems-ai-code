package com.cheems.cheemsaicode.controller;

import com.cheems.cheemsaicode.annotation.AuthCheck;
import com.cheems.cheemsaicode.common.BaseResponse;
import com.cheems.cheemsaicode.constant.UserConstant;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.cheems.cheemsaicode.utils.ResultUtils;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.cheems.cheemsaicode.model.entity.ChatHistory;
import com.cheems.cheemsaicode.service.ChatHistoryService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 对话历史 控制层。
 *
 * @author cheems
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    /**
     * 保存对话历史。
     *
     * @param chatHistory 对话历史
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody ChatHistory chatHistory) {
        return chatHistoryService.save(chatHistory);
    }

    /**
     * 根据主键删除对话历史。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return chatHistoryService.removeById(id);
    }

    /**
     * 根据主键更新对话历史。
     *
     * @param chatHistory 对话历史
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ChatHistory chatHistory) {
        return chatHistoryService.updateById(chatHistory);
    }

    /**
     * 查询所有对话历史。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<ChatHistory> list() {
        return chatHistoryService.list();
    }

    /**
     * 根据主键获取对话历史。
     *
     * @param id 对话历史主键
     * @return 对话历史详情
     */
    @GetMapping("getInfo/{id}")
    public ChatHistory getInfo(@PathVariable Long id) {
        return chatHistoryService.getById(id);
    }

    /**
     * 分页查询对话历史。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ChatHistory> page(Page<ChatHistory> page) {
        return chatHistoryService.page(page);
    }

    /**
     * 分页查询指定应用的对话历史（按时间升序排序）
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 分页对象
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<ChatHistory>> listChatHistoryByPage(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(chatHistoryQueryRequest.getAppId() == null, ErrorCode.PARAMS_ERROR, "应用ID不能为空");

        int pageNum = chatHistoryQueryRequest.getPageNum();
        int pageSize = chatHistoryQueryRequest.getPageSize();

        // 默认按创建时间降序排序
        if (chatHistoryQueryRequest.getSortField() == null) {
            chatHistoryQueryRequest.setSortField("createTime");
        }
        if (chatHistoryQueryRequest.getSortOrder() == null) {
            chatHistoryQueryRequest.setSortOrder("descend");
        }

        Page<ChatHistory> page = chatHistoryService.page(
                Page.of(pageNum, pageSize),
                chatHistoryService.getQueryWrapper(chatHistoryQueryRequest)
        );

        return ResultUtils.success(page);
    }

    /**
     * 【管理员】分页查询所有对话历史（按时间降序排序）
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 分页对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/admin")
    public BaseResponse<Page<ChatHistory>> listChatHistoryByPageAdmin(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);

        int pageNum = chatHistoryQueryRequest.getPageNum();
        int pageSize = chatHistoryQueryRequest.getPageSize();

        // 默认按创建时间降序排序
        if (chatHistoryQueryRequest.getSortField() == null) {
            chatHistoryQueryRequest.setSortField("createTime");
        }
        if (chatHistoryQueryRequest.getSortOrder() == null) {
            chatHistoryQueryRequest.setSortOrder("descend");
        }

        Page<ChatHistory> page = chatHistoryService.page(
                Page.of(pageNum, pageSize),
                chatHistoryService.getQueryWrapper(chatHistoryQueryRequest)
        );

        return ResultUtils.success(page);
    }

}
