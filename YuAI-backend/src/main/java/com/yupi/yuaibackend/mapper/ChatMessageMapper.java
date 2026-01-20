package com.yupi.yuaibackend.mapper;

import com.yupi.yuaibackend.model.entity.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 17832
* @description 针对表【chat_message(聊天消息表)】的数据库操作Mapper
* @createDate 2026-01-19 17:10:46
* @Entity com.yupi.yuaibackend.model.entity.ChatMessage
*/
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

}




