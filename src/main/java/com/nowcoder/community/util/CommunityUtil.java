package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author : zhiHao
 * @since : 2021/9/25
 */
public class CommunityUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommunityUtil.class);

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String md5(String key) {
        if(StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String convert2JSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }
        }
        return json.toJSONString();
    }

    public static String convert2JSONString(int code, String msg) {
        return convert2JSONString(code, msg, null);
    }

    public static String convert2JSONString(int code) {
        return convert2JSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 25);
        logger.info("JSON: " + convert2JSONString(0, "msg", map));
    }
}
