package com.nowcoder.community.util;

/**
 * @author : zhiHao
 * @since : 2022/5/26
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    public static String generateEntityLikeKey (int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String generateUserLikeKey (int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    public static String generateFolloweeKey (int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String generateFollowerKey (int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String generateKaptcha (String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    public static String generateTicketKey (String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String generateUserKey (int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
