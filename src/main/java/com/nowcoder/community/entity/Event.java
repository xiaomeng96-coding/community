package com.nowcoder.community.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhiHao
 * @since : 2022/5/29
 */
@Data
public class Event {

    private String topic;
    private int userId;
    private int entityType;
    private int entityId;
    private int entityUserId;
    private Map<String, Object> data = new HashMap<>();

}
