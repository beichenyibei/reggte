package com.ithema.reggie.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisTemplateConfig {

    @Autowired
    private RedisTemplate redisTemplate;

    @Bean
    public RedisTemplate redisTemplateInit() {
        //设置序列化Key的实例化对象
        redisTemplate.setKeySerializer(new GenericFastJsonRedisSerializer());
        //设置序列化Value的实例化对象
        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
        //设置序列化HashKey的实例化对象 不用hash结构这俩不用搞了
        redisTemplate.setHashKeySerializer(new GenericFastJsonRedisSerializer());
        //设置序列化Hash Value的实例化对象
        redisTemplate.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        //对object 类型的序列化
        redisTemplate.setDefaultSerializer(new FastJsonRedisSerializer<>(Object.class));
        return redisTemplate;
        //GenericFastJsonRedisSerializer 序列化器
        // 不用fastJSON也可以用 springboot 自带的Jackson 的 GenericJackson2JsonRedisSerializer
    }


}

