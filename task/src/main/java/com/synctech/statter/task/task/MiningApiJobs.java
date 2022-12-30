package com.synctech.statter.task.task;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class MiningApiJobs {

    @Resource
    JedisService jedisService;

    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "MiningApiJobs.expireAll", lockAtLeastFor = 100, lockAtMostFor = 60000)
    public void expireAll() {
        log.info("Timing task: : Start to expire all access key and access info ...");
        jedisService.del(CacheKey.CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS);
        jedisService.del(CacheKey.CACHEKEY_AK_PROMOTION_BY_AK);
    }

    @Scheduled(fixedDelay = 60000)
    @SchedulerLock(name = "MiningApiJobs.expireAK", lockAtLeastFor = 100, lockAtMostFor = 60000)
    public void expireAK() {
        log.info("Timing task: : Start to expire access key ...");
        Jedis j = jedisService.get();
        try {
            Map<String, String> allAks = j.hgetAll(CacheKey.CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS);
            if (CollectionUtils.isEmpty(allAks)) return;
            long n = System.currentTimeMillis();
            for (Map.Entry<String, String> entry : allAks.entrySet()) {
                JSONObject info = JSONObject.parseObject(entry.getValue());
                if (n > info.getLongValue("etl")) {
                    j.hdel(CacheKey.CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS, entry.getKey());// expire access key
                    j.hdel(CacheKey.CACHEKEY_AK_PROMOTION_BY_AK, info.getString("ak"));// expire prom info
                }
            }
        } finally {
            jedisService.flush(j);
        }
    }

}
