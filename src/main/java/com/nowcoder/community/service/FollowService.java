package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author : zhiHao
 * @since : 2022/5/27
 */
@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.generateFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.generateFollowerKey(entityType, entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return redisOperations.exec();
            }
        });
    }

    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.generateFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.generateFollowerKey(entityType, entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey, entityId);
                redisOperations.opsForZSet().remove(followerKey, userId);
                return redisOperations.exec();
            }
        });
    }

    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.generateFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.generateFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.generateFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.generateFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet()
                .reverseRange(followeeKey, offset, offset + limit - 1);
        if (CollectionUtils.isEmpty(targetIds)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.selectUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String folowerKey = RedisKeyUtil.generateFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate
                .opsForZSet().reverseRange(folowerKey, offset, limit + offset - 1);
        if (CollectionUtils.isEmpty(targetIds)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.selectUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(folowerKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            result.add(map);
        }
        return result;
    }
}
