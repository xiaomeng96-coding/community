package com.nowcoder.community.entity;

import com.nowcoder.community.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zhiHao
 * @since : 2021/9/20
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User selectUserById(int id) {
        return userMapper.selectById(id);
    }
}
