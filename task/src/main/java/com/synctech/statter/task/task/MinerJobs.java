package com.synctech.statter.task.task;

import com.synctech.statter.base.entity.CpuModel;
import com.synctech.statter.base.mapper.CpuModuleMapper;
import com.synctech.statter.base.mapper.MinerMapper;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MinerJobs {

    @Resource
    JedisService jedisService;

    @Resource
    MinerMapper minerMapper;

    @Resource
    CpuModuleMapper cpuModuleMapper;

    @Scheduled(fixedDelay = 3600000)
    @SchedulerLock(name = "MinerJobs.cacheSn", lockAtLeastFor = 100, lockAtMostFor = 600000)
    public void cacheSn() {
        log.info("Timing task: start to cache full SN code to redis ...");
        List<String> sns = minerMapper.findAllSn();
        if (CollectionUtils.isEmpty(sns)) return;
        Map<String, String> c = sns.stream().collect(Collectors.toMap(Function.identity(), s -> "1"));
        jedisService.hsetAll(CacheKey.CACHEKEY_INFO_MINER_SN_MAP, c);// Incremental update
    }

    @Scheduled(fixedDelay = 3600000)
    @SchedulerLock(name = "MinerJobs.cacheCpuModule", lockAtLeastFor = 100, lockAtMostFor = 600000)
    public void cacheCpuModule() {
        log.info("Timing task: start to cache full cpu module to redis ...");
        List<CpuModel> ms = cpuModuleMapper.findAll();
        if (CollectionUtils.isEmpty(ms)) return;
        for (CpuModel m : ms) {
            jedisService.hset(CacheKey.CACHEKEY_INFO_CPU_MODEL, m.getCpuModelName(), m);// a little data,no need to care the performance
        }
    }

    /**
     * Clean up once a day every day to refresh or correct the error cache information
     */
    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "MinerJobs.cleanMinerInfo", lockAtLeastFor = 100, lockAtMostFor = 600000)
    public void cleanMinerInfo() {
        log.info("Timing task: Start to flush all mining machine information ...");
        jedisService.del(CacheKey.CACHEKEY_INFO_MINER_BY_SN);
    }


}
