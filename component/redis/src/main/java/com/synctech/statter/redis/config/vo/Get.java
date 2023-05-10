package com.synctech.statter.redis.config.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class Get<R> {

    String lockKey;
    String key;

    Class<R> resultClazz;

}
