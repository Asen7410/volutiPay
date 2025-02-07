package com.tr.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Asen
 */
@Component
public class RedisUtil {


    private static RedisTemplate template;
    /** key string value Object */
    private static ValueOperations<String,Object> valueOperations;

    /** 不设置过期时长 */
    private final static long NOT_EXPIRE = -1;

    @Autowired
    public RedisUtil(RedisTemplate redisTemplate){
        template = redisTemplate;
        valueOperations = template.opsForValue();
    }
    /**
     * 功能描述: 获取RedisTemplate
     */
    public static RedisTemplate getTemplate(){
        return template;
    }
    /**
     * 功能描述: 递增方法
     */
    /*public static Long increment(String key){
        return valueOperations.increment(key);
    }*/
    public static Long increment(String key,long delta){
        return valueOperations.increment(key,delta);
    }
    /**
      *  自增设置时间
     */
    /*public static Long incrementAndTime(String key,long expire){
        Long increment = valueOperations.increment(key);
        if(null != increment){
            setExpire(key,expire);
        }
        return increment;
    }*/
    /**
     * 功能描述: 校验key是否过期
     */
    public static boolean hasKey(String key) {
        return template.hasKey(key);
    }

    /**
     * redis放入值 Integer、Long、Double、Float、Byte、Short 转字符串存放
     */
    public static void set(String key, Object value) {
        set(key, value, NOT_EXPIRE);
    }
    public static void set(String key, Object value, long expire) {
        //默认秒
        set(key,value,expire,TimeUnit.SECONDS);
    }
    /**
     * imeUnit DAYS 天、HOURS 小时、MINUTES 分、SECONDS 秒、MILLISECONDS 毫秒
     */
    public static void set(String key, Object value, long expire,TimeUnit timeUnit) {
        valueOperations.set(key, value);
        if (expire != NOT_EXPIRE) {
            setExpire(key, expire, timeUnit);
        }
    }
    /**
     * 功能描述: 设置key过期时间
     */
    public static void setExpire(String key,long expire){
        setExpire(key,expire,TimeUnit.SECONDS);
    }
    public static void setExpire(String key,long expire,TimeUnit timeUnit){
        if (expire != NOT_EXPIRE) {
            template.expire( key, expire, timeUnit);
        }
    }

    /**
     * 功能描述: 删除redisKey
     */
    public static void delete(String key) {
        template.delete(key);
    }

    /**
     * redis发布 输出时没有顺序
     */
    public static void convertAndSend(String channel, String message) {
        template.convertAndSend(channel,message);
    }

    /**
     * 获取命名空间下的所有key
     */
    public static Set<String> keys(String key){
        return template.keys(key + "*");
    }
    /**
     * 功能描述: 获取 ttl值
     */
    public Long ttl(String key) {
        return template.getExpire(key);
    }
    /**
     * Description: 如果key存在, 则返回false, 如果不存在, 则将key=value放入redis中, 并返回true
     */
    public static boolean setIfAbsent(String key, String value) {
        return valueOperations.setIfAbsent( key, value);
    }

    /**
     * Description: 返回KEY是否存在
     */
    public static Boolean exists(String key) {
        if (null == key || key.isEmpty() ||  "null".equalsIgnoreCase(key)){
            return false;
        }
        return template.hasKey( key);
    }

    /**
     * 功能描述: 获取值
     * 接受类型为BigDecimal、Integer、Long、Double、Float、Byte、Short 用下面一个方法 手动转对象
     */
    public static <T> T get(String key) {
        Object value = valueOperations.get(key);
        if(null == value){
            return null;
        }
        return (T)value;
    }


    /**
     * 功能描述: 手动转对象
     */
    public static  <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        return value == null ? null : fromJson(JSON.toJSONString(value), clazz);
    }
    /**
     * 功能描述: 手动转集合
     */
    public static  <T> List<T> getArray(String key, Class<T> clazz) {
        Object value = get(key);
        return value == null ? null : fromJsonArray(JSON.toJSONString(value), clazz);
    }

    /**
     * Description: JSON数据，转成Object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
    /**
     * Description: JSON数据，转成List集合
     */
    public static <T> List<T>  fromJsonArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * 功能描述: 向 Redis Set 中添加元素
     */
    public static void addToSet(String key, Object value) {
        template.opsForSet().add(key, value);
    }

    /**
     * 功能描述: 从 Redis Set 中移除元素
     */
    public static void removeFromSet(String key, Object value) {
        template.opsForSet().remove(key, value);
    }

    /**
     * 功能描述: 获取 Redis Set 中所有元素
     */
    public static Set<Object> getSetMembers(String key) {
        return template.opsForSet().members(key);
    }

    /**
     * 功能描述: 判断元素是否在 Redis Set 中
     */
    public static boolean isMemberOfSet(String key, Object value) {
        return template.opsForSet().isMember(key, value);
    }

    /**
     * 功能描述: 获取 Redis Set 的大小
     */
    public static Long getSetSize(String key) {
        return template.opsForSet().size(key);
    }

    public static void lpushAll(String key, String... values) {
        template.opsForList().leftPushAll(key, values);
    }

    public static String lpop(String key) {
        return (String) template.opsForList().leftPop(key);
    }
}
