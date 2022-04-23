package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

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
        List<Integer> list = getLetterIds(messages);
        if ( !CollectionUtils.isEmpty(list)) {
            messageService.readMessage(list);
        }
        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] userIds = conversationId.split("_");
        int fromId = hostHolder.getUser().getId();
        return fromId == Integer.parseInt(userIds[0]) ? userService.selectUserById(Integer.parseInt(userIds[1])) :
                userService.selectUserById(Integer.parseInt(userIds[0]));
    }

    private List<Integer> getLetterIds(List<Message> letters) {
        List<Integer> list = new ArrayList<>();
        if ( !CollectionUtils.isEmpty(letters)) {
            for (Message message : letters) {
                if (hostHolder.getUser().getId() == message.getToId() &&
                    message.getStatus() == 0) {
                    list.add(message.getId());
                }
            }
        }
        return list;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.selectUserByName(toName);
        if (target == null) {
            return CommunityUtil.convert2JSONString(1, "目标不存在！");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setConversationId(createConversationId(message.getFromId(), message.getToId()));
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.convert2JSONString(0);
    }

    private String createConversationId (int fromId, int toId) {
        return fromId > toId ? toId + "_" + fromId : fromId + "_" + toId;
    }
}
