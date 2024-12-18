package com.statter.statter.redis.jedis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisPool {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Bean
    public JedisPool jedisPool() {
        JedisPool jedisPool = new JedisPool(this.host, Integer.valueOf(this.port));
        return jedisPool;
    }

}
