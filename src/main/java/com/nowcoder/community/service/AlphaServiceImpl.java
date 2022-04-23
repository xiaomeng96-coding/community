package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * @author : zhiHao
 * @since : 2021/9/19
 */
@Service
// @Scope("protoType")
public class AlphaServiceImpl implements AlphaService{

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaServiceImpl() {
        // System.out.println("实例化AlphaService");
    }

    @Override
    @PostConstruct
    public void init() {
        // System.out.println("初始化AlphaService");
    }

    @Override
    @PreDestroy
    public void destroy() {
        // System.out.println("销毁AlphaService");
    }

    public String selectByAlphaDao() {
        return alphaDao.selectByUserId();
    }

    /*
    * REQUIRED: 支持当前事务（外部事务），如果不存在则创建新事务。
    * REQUIRED_NEW: 创建一个新的事务，暂停当前事务（外部事务）
    * NESTED: 如果当前存在事务（外部事务），则嵌套在该事务中执行（独立的提交和回滚），否则就和REQUIRED一样
    * */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save() {
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/100t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("hello");
        discussPost.setContent("hello");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);

        Integer.valueOf("abc");
        return "ok";
    }

    public Object saveTransaction() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                User user = new User();
                user.setUsername("alphaOne");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("alphaOne@qq.com");
                user.setHeaderUrl("http://images.nowcoder.com/head/1241t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                DiscussPost discussPost = new DiscussPost();
                discussPost.setUserId(user.getId());
                discussPost.setTitle("helloAlpha");
                discussPost.setContent("helloAlpha");
                discussPost.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(discussPost);

                Integer.valueOf("abc");
                return "ok";
            }
        });
    }
}
