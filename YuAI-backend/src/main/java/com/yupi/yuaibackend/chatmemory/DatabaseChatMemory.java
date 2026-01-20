package com.yupi.yuaibackend.chatmemory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yupi.yuaibackend.service.ChatMessageRepository;
import com.yupi.yuaibackend.common.MessageConverter;
import com.yupi.yuaibackend.model.entity.ChatMessage;
import com.yupi.yuaibackend.service.ChatMessageRepository;
import lombok.SneakyThrows;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseChatMemory implements ChatMemory {

    //
    private final ChatMessageRepository chatMessageRepository;

    // 添加构造函数
    public DatabaseChatMemory(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }


    /**
     * 新增消息
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<ChatMessage> chatMessages = messages.stream()
                // 转换：从message转为ChatMessage，调用MessageConverter的方法
                .map(message -> MessageConverter.toChatMessage(message, conversationId))
                .collect(Collectors.toList());

        chatMessageRepository.saveBatch(chatMessages, chatMessages.size());
    }


    /**
     * 获取信息
     * @param conversationId
     * @param lastN
     * @return
     */
    @SneakyThrows
    @Override
    public List<Message> get(String conversationId, int lastN) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        // 查询最近的lastN条消息
        queryWrapper.eq(ChatMessage::getConversationId, conversationId)
                .orderByDesc(ChatMessage::getCreateTime)
                .last(lastN > 0, "LIMIT " + lastN);

        List<ChatMessage> chatMessages = chatMessageRepository.list(queryWrapper);

        // 然后按照时间顺序返回
        if (!chatMessages.isEmpty()) {
            Collections.reverse(chatMessages);
        }
        return chatMessages.stream()
                .map(MessageConverter::toMessage)
                .collect(Collectors.toList());
    }


    /**
     * 删除消息
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        chatMessageRepository.remove(queryWrapper);
    }
}
