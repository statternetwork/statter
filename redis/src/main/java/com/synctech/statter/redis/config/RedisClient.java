package com.synctech.statter.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

//@Component
public class RedisClient {

    private static Logger logger = LoggerFactory.getLogger(RedisClient.class);

    //    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RedisClient(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Specify the cache failure time
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public Set<String> matchingKeys(String keyPattern) {
        return redisTemplate.keys(keyPattern);
    }

    public boolean haskey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * Ordinary cache obtain
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * Ordinary cache put
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Putting and setting time for ordinary cache
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}
