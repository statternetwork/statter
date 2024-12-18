package com.statter.statter.common.service.service;

import com.statter.statter.common.pool.vo.PoolTask;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuestionService {

    @Autowired
    JedisService jedisService;

    /**
     * Get the block height, if exist cache ,priority use cache
     *
     * @return long
     */
    public long getBlockIndex() {
        return getPoolTask().getBlock().getBlockIndexValue();
    }

    public PoolTask getPoolTask() {
        PoolTask pt = jedisService.get(CacheKey.CACHEKEY_MINING_QUESTION, PoolTask.class);
        if (null == pt) {
            throw new AppBizException(10011, "question has not cache");
        }
        return pt;
    }

}
