package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : zhiHao
 * @since : 2022/4/22
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetters(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRowCounts(messageService.selectConversationCount(user.getId()));
        List<Message> messages = messageService.selectConversations(user.getId(),
                page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if ( !CollectionUtils.isEmpty(messages)) {
            for (Message message : messages) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.selectLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.selectLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.selectUserById(targetId));
                conversations.add(map);
            }
            model.addAttribute("conversations", conversations);
            int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
            model.addAttribute("letterUnreadCount", letterUnreadCount);
        }
        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRowCounts(messageService.selectLetterCount(conversationId));

        List<Message> messages = messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if ( !CollectionUtils.isEmpty(messages)) {
            for (Message message : messages) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.selectUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));
        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] userIds = conversationId.split("_");
        int fromId = hostHolder.getUser().getId();
        return fromId == Integer.parseInt(userIds[0]) ? userService.selectUserById(Integer.parseInt(userIds[1])) :
                userService.selectUserById(Integer.parseInt(userIds[0]));
    }
}
