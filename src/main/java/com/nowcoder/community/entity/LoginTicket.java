package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : zhiHao
 * @since : 2022/4/14
 */
@Data
public class LoginTicket {

    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
