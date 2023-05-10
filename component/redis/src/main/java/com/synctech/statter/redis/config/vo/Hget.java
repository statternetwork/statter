package com.synctech.statter.redis.config.vo;

import lombok.Data;

@Data
public class Hget<R> {

    String lockKey;
    String key;
    String field;

    Class<R> resultClazz;

    public Hget(String key, String field, String lockKey, Class<R> resultClazz) {
        this.key = key;
        this.field = field;
        this.lockKey = lockKey;
        this.resultClazz = resultClazz;
    }

}
