package com.synctech.statter.task.task;

import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MiningApiJobs {

    @Autowired
    JedisService jedisService;

    @Value("${statter.promotion.ak.old.usable:true}")
    private Boolean akOldUsable;

    @Scheduled(fixedDelay = 60000)
    // @SchedulerLock(name = "MiningApiJobs.expireAK", lockAtLeastFor = 100, lockAtMostFor = 60000)
    public void expireAK() {
        log.info("Timing task: : Start to expire access key ...");
        Jedis j = jedisService.get();
        try {
            Map<String, String> allAks = j.hgetAll(CacheKey.CACHEKEY_AK);
            if (CollectionUtils.isEmpty(allAks))
                return;
            long n = System.currentTimeMillis();
            Map<String, String> latestAk = new HashMap<>();
            for (Map.Entry<String, String> entry : allAks.entrySet()) {
                long expireTime = Long.parseLong(entry.getValue().split("::")[0]);
                if (n > expireTime) {
                    j.hdel(CacheKey.CACHEKEY_AK, entry.getKey());// expire ak should be removed
                    continue;
                }
                if (akOldUsable)
                    continue;// the old ak is effective
                // the old ak is not usable, delete old ak, one promotion one ak
                String pa = entry.getValue().split("::")[1];
                if (latestAk.containsKey(pa)) {
                    String[] v = latestAk.get(pa).split("::");// the ak in `latestAk`
                    if (Long.parseLong(v[0]) > expireTime) {
                        j.hdel(CacheKey.CACHEKEY_AK, entry.getKey());// the current ak is earlier than the ak in
                                                                     // `latestAk`, it should be removed
                        continue;
                    } else {
                        j.hdel(CacheKey.CACHEKEY_AK, v[1]); // the current is later, and the ak in `latestAk` should be
                                                            // removed
                    }
                }
                latestAk.put(pa, expireTime + "::" + entry.getKey());// tag the current ak into `latestAk`
            }
        } finally {
            jedisService.flush(j);
        }
    }

}
