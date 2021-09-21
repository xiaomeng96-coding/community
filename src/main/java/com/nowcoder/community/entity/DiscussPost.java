package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : zhiHao
 * @since : 2021/9/20
 */
@Data
public class DiscussPost {

    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
