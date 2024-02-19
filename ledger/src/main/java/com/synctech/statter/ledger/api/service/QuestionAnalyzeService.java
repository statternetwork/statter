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
public class QuestionAnalyzeService {

    @Autowired
    JedisService jedisService;

    @Autowired
    PoolService poolService;

    @Value("${statter.task.fixed-load:}")
    String statterTaskFixedLoad;
    @Value("${statter.task.difficulty-decrease}")
    int statterTaskDifficultyDecrease;

    public String analyzeWorkload(String poolWorkload) {
        if (StringUtils.isNotBlank(this.statterTaskFixedLoad)) {
            // If a fixed mining machine task load is specified, use the fixed value
            return this.statterTaskFixedLoad;
        } else if (this.statterTaskDifficultyDecrease != 0) {
            // If the fixed task is not specified, but the difficulty of floating value is
            // specified, the value is calculated with this value
            return poolWorkload.substring(0, poolWorkload.length() + statterTaskDifficultyDecrease);
        }
        return poolWorkload;
    }

}
