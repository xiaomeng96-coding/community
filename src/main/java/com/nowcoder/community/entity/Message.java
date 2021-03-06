package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : zhiHao
 * @since : 2022/4/22
 */
@Data
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;

}
