package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author : zhiHao
 * @since : 2022/4/19
 * 持有用户信息，代替session对象
 */
@Component
public class HostHolder {

    private final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void setUser(User user) {
        userThreadLocal.set(user);
    }

    public User getUser() {
        return userThreadLocal.get();
    }

    public void clear() {
        userThreadLocal.remove();
    }
}
