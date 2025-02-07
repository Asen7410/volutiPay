package com.tr.util;

/**
 * 常量
 */
public class Constants {

    //redis 存放红包信息
    public static final String RED_ENVELOPE_INFO = "red_envelope_info_%s:";

    //redis key AccessToken
    public static final String ACCESS_TOKEN = "AccessToken:";

    //redis key 红包ID:用户集合的
    public static final String RED_ENVELOPE_USER_KEY_FORMAT = "red_envelope:%d:users";

    //红包ID的分布式锁
    public static final String RED_ENVELOPE_LOCK = "red_envelope_lock_%s";

    public static final String RED_ENVELOPE_AMOUNTS = "red_envelope_amounts_%s:";


}

