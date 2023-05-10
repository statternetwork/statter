package com.synctech.statter.ledger.api.service;


import cn.hutool.core.util.NumberUtil;
import com.synctech.statter.common.pool.service.PoolService;
import com.synctech.statter.common.pool.vo.PoolTask;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.config.vo.Hget;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuestionService {


    @Autowired
    JedisService jedisService;

    @Autowired
    PoolService poolService;

    @Value("${statter.task.fixed-load:}")
    String statterTaskFixedLoad;
    @Value("${statter.task.difficulty-decrease}")
    int statterTaskDifficultyDecrease;


    /**
     * Get the block height, if exist cache ,priority use cache
     *
     * @return long
     */
    public long getBlockIndex() {
        String v = jedisService.get(CacheKey.CACHEKEY_MINING_BLOCK_INDEX);
        return NumberUtil.parseLong(v);
    }

    public PoolTask getPoolTask(long blockIndex, String walletAddress, String hash) {
        String k = CacheKey.CACHEKEY_MINING_QUESTION_KEY_PREFIX + blockIndex;
        String lk = CacheKey.CACHEKEY_MINING_QUESTION_LOCK + walletAddress;
        PoolTask pt = jedisService.hget(new Hget<>(k, walletAddress, lk, PoolTask.class),
                p -> poolService.getPoolTaskImpl(blockIndex, walletAddress, hash));
        if (null == pt || blockIndex != NumberUtil.parseLong(pt.getBlock().getBlockIndex()))
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_GET_POOL_TASK_NOT_MATCH_BLOCK_INDEX);
        return pt;
    }

    public void deletePoolTask(long blockIndex, String walletAddress) {
        jedisService.hdel(CacheKey.CACHEKEY_MINING_QUESTION_KEY_PREFIX + blockIndex, walletAddress);
    }

    public String analyzeWorkload(String poolWorkload) {
        if (StringUtils.isNotBlank(this.statterTaskFixedLoad)) {
            // If a fixed mining machine task load is specified, use the fixed value
            return this.statterTaskFixedLoad;
        } else if (this.statterTaskDifficultyDecrease != 0) {
            // If the fixed task is not specified, but the difficulty of floating value is specified, the value is calculated with this value
            return poolWorkload.substring(0, poolWorkload.length() + statterTaskDifficultyDecrease);
        }
        return poolWorkload;
    }

}
