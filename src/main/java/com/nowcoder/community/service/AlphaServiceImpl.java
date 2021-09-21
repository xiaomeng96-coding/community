package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author : zhiHao
 * @since : 2021/9/19
 */
@Service
// @Scope("protoType")
public class AlphaServiceImpl implements AlphaService{

    @Autowired
    private AlphaDao alphaDao;

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
}
