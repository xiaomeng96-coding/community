package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author : zhiHao
 * @since : 2021/9/19
 */
@Repository("AlphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao{

    @Override
    public String selectByUserId() {
        return "Hibernate";
    }

}
