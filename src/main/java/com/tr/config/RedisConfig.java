package com.tr.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String,Object> template = new RedisTemplate<>();

        // Jackson2JsonRedisSerializer 是用于将对象转换为 JSON 格式，或者从 JSON 格式转换为对象
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();

        // 配置 ObjectMapper 来处理 JSON 序列化和反序列化
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);  // 设置可见性
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // 设置反序列化时忽略未知属性
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // StringRedisSerializer 用于将 String 序列化为 Redis 存储时的字符串
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 设置 RedisTemplate 的 Key 和 Value 的序列化方式
        template.setKeySerializer(stringRedisSerializer);  // 对 key 使用 StringRedisSerializer
        template.setHashKeySerializer(stringRedisSerializer);  // 对 hash key 使用 StringRedisSerializer
        template.setValueSerializer(jackson2JsonRedisSerializer);  // 对 value 使用 Jackson2JsonRedisSerializer
        template.setHashValueSerializer(jackson2JsonRedisSerializer);  // 对 hash value 使用 Jackson2JsonRedisSerializer

        // 设置 Redis 连接工厂
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();  // 初始化 RedisTemplate

        return template;
    }

}
