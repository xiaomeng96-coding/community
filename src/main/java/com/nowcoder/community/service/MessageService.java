package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : zhiHao
 * @since : 2022/4/22
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public List<Message> selectConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int selectConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> selectLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int selectLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int selectLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }
}
