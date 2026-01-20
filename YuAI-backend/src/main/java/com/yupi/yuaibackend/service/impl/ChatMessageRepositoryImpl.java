package com.yupi.yuaibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuaibackend.model.entity.ChatMessage;
import com.yupi.yuaibackend.service.ChatMessageRepository;
import com.yupi.yuaibackend.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author 17832
* @description 针对表【chat_message(聊天消息表)】的数据库操作Service实现
* @createDate 2026-01-19 17:10:46
*/
@Service
public class ChatMessageRepositoryImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageRepository {


}




