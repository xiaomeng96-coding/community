package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @author : zhiHao
 * @since : 2021/9/20
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/100t.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        userMapper.updateHeader(150, "http://images.nowcoder.com/head/101t.png");
        userMapper.updateStatus(150, 1);
        userMapper.updatePassword(150, "hello");
    }

    @Test
    public void testUpdateUserPassword() {
        String password = CommunityUtil.md5("1234564bd27");
        userMapper.updatePassword(157, password);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPosts =
                discussPostMapper.selectDiscussPosts(0, 0 , 10);
        for(DiscussPost discussPost: discussPosts) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testConversations() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }
    }

    @Test
    public void testConversationCount() {
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
    }

    @Test
    public void testLetters() {
        List<Message> messages = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : messages) {
            System.out.println(message);
        }
    }

    @Test
    public void testLetterCount() {
        int count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
    }

    @Test
    public void testLetterUnreadCount() {
        int count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);
    }
}
