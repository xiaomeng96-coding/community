package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author : zhiHao
 * @since : 2022/4/22
 */
@Controller
public class MessageController implements CommunityConstant {

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
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            model.addAttribute("noticeUnreadCount", noticeUnreadCount);
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

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            messageVO.put("user", userService.selectUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", unread);
            model.addAttribute("commentNotice", messageVO);
        }

        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            messageVO.put("user", userService.selectUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unread);
            model.addAttribute("likeNotice", messageVO);
        }

        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            messageVO.put("user", userService.selectUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unread);
            model.addAttribute("followNotice", messageVO);
        }

        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRowCounts(messageService.findNoticeCount(user.getId(), topic));
        List<Message> noticeList = messageService
                .findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if ( !CollectionUtils.isEmpty(noticeList)) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.selectUserById( (Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                map.put("fromUser", userService.selectUserById(notice.getFromId()));
                noticeVoList.add(map);
            }
            model.addAttribute("notices", noticeVoList);
        }

        List<Integer> ids = getLetterIds(noticeList);
        if ( !CollectionUtils.isEmpty(ids)) {
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
