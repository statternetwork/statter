package com.synctech.statter.task.task;

import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.base.mapper.CpuModuleMapper;
import com.synctech.statter.base.mapper.PromotionMapper;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PromotionJobs {

    @Resource
    JedisService jedisService;

    @Resource
    CpuModuleMapper cpuModuleMapper;

    @Resource
    PromotionMapper promotionMapper;

    @Scheduled(fixedDelay = 60000)
    @SchedulerLock(name = "PromotionJobs.cacheAll", lockAtLeastFor = 100, lockAtMostFor = 60000)
    public void cacheAll() {
        log.info("Timing task: : Start to cache all promotion info ...");
        List<Promotion> all = promotionMapper.findAll();
        if (CollectionUtils.isEmpty(all)) return;
        jedisService.process(j -> {
            for (Promotion p : all) {
                j.hset(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, p.getAddress(), p.toString());
            }
        });
    }

    /**
     * Statistics the number of mining machines in each mining pool
     */
    @Scheduled(fixedDelay = 3600000)
    @SchedulerLock(name = "PromotionJobs.countMiners", lockAtLeastFor = 100, lockAtMostFor = 60000)
    public void countMiners() {
        log.info("Timing task: : Start to count miners under promotion ...");
        List<Promotion> all = promotionMapper.findAll();
        List<Promotion> cs = promotionMapper.countMiners();
        Map<String, Integer> mcm = cs.stream().collect(Collectors.toMap(Promotion::getAddress, Promotion::getMinerCount));
        List<Promotion> hs = promotionMapper.countHash();
        Map<String, Long> hcm = hs.stream().collect(Collectors.toMap(Promotion::getAddress, Promotion::getHash));
        for (Promotion p : all) {
            int c = mcm.containsKey(p.getAddress()) ? mcm.get(p.getAddress()) : 0;
            long h = hcm.containsKey(p.getAddress()) ? hcm.get(p.getAddress()) : 0;
            promotionMapper.updateMinerCount(p.getAddress(), c, h);
            p.setMinerCount(c).setHash(h);
            jedisService.hset(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, p.getAddress(), p);
        }
    }

    /**
     * Clean up once a day every day to refresh or correct the error cache information
     */
    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "PromotionJobs.cleanMinerInfo", lockAtLeastFor = 100, lockAtMostFor = 600000)
    public void cleanMinerInfo() {
        log.info("Timing task: Start to flush all promotion api access key fetch info ...");
        jedisService.del(CacheKey.CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS);
    }

}
