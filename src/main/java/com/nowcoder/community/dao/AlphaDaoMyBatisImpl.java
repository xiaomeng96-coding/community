package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author : zhiHao
 * @since : 2021/9/19
 */
@Repository
@Primary
public class AlphaDaoMyBatisImpl implements AlphaDao{

    @Override
    public String selectByUserId() {
        return "MyBatis";
    }

}
