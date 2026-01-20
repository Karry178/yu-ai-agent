package com.yupi.yuaibackend.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuaibackend.model.entity.ChatMessage;
import org.springframework.ai.chat.messages.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 通用转换方法：实现ChatMessage与Message之间的转换
 */
public class MessageConverter {

    // 用于JSON序列化与反序列化
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将 Message 转换为 ChatMessage
     * @param message
     * @param conversationId
     * @return
     */
    public static ChatMessage toChatMessage(Message message, String conversationId) {
        // 新建ChatMessage对象
        ChatMessage chatMessage = new ChatMessage();

        /*return ChatMessage.builder()
                // 获取conversionId，拿到对应的回答
                .conversionId(conversationId)
                // 拿到信息类型messageType
                .messageType(message.getMessageType())
                .content(message.getText())
                .metadata(message.getMetadata())
                .build();*/

        // 不再用build构造，而是用setter方法构造参数
        chatMessage.setConversationId(conversationId);  // 设置会话Id
        chatMessage.setMessageType(message.getMessageType().getValue());  // 设置消息类型
        chatMessage.setContent(message.getText());  // 设置消息内容

        // 将Map转换为JSON字符串存储
        try {
            String metadataJson = objectMapper.writeValueAsString(message.getMetadata());
            chatMessage.setMetadata(metadataJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            chatMessage.setMetadata("{}");  // 失败时设置空对象
        }
        return chatMessage;
    }


    /**
     * 将 ChatMessage 转换为 Message
     * @param chatMessage
     * @return
     * @throws IllegalAccessException
     */
    public static Message toMessage(ChatMessage chatMessage) {
        // 先获取chatMessage的类型
        String messageType = chatMessage.getMessageType();
        // 然后获取chatMessage内容
        String text = chatMessage.getContent();
        // 获取chatMessage的元数据
        // Map<String, Object> metadata = chatMessage.getMetadata();

        // 将JSON转化为Map
        Map<String, Object> metadata = new HashMap<>();
        try {
            // 首先拿到元数据的JSON格式
            String metadataJson = chatMessage.getMetadata();
            // 判断
            if (metadataJson != null && !metadataJson.isEmpty()) {
                // ✅ 修改 2: 使用正确的 readValue 方法
                metadata = objectMapper.readValue(
                        metadataJson,
                        new TypeReference<Map<String, Object>>() {}
                );
            }
        } catch (JsonProcessingException e) {
                e.printStackTrace();
        }

        // switch语句判断要转换的内容是什么类型：
        return switch (messageType) {
            case "user" -> new UserMessage(text);
            case "assistant" -> new AssistantMessage(text, metadata);
            case "system" -> new SystemMessage(text);
            case "tool" -> new ToolResponseMessage(List.of(), metadata);
            default -> throw new IllegalArgumentException("未识别的消息类型：" + messageType);
        };
    }
}
