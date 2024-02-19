package com.synctech.statter.task.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.CpuModel;
import com.synctech.statter.base.entity.Miner;
import com.synctech.statter.base.entity.Process;
import com.synctech.statter.base.entity.White;
import com.synctech.statter.base.mapper.CpuModuleMapper;
import com.synctech.statter.base.mapper.MinerMapper;
import com.synctech.statter.base.mapper.WhiteMapper;
import com.synctech.statter.common.service.service.ProcessService;
import com.synctech.statter.common.service.service.WhiteService;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MinerJobs {

    @Value("${statter.mining.pledge.unneeded-datetime-range:}")
    String miningPledgeUnneededDatetimeRange;

    @Autowired
    JedisService jedisService;

    @Autowired
    WhiteService whiteService;

    @Autowired
    MinerMapper minerMapper;

    @Autowired
    ProcessService processService;

    @Autowired
    CpuModuleMapper cpuModuleMapper;

    @Autowired
    WhiteMapper whiteMapper;

    @Scheduled(fixedDelay = 3600000, initialDelay = -100)
    public void cacheMiners() {
        log.info("=======================================================");
        whiteService.cacheWhiteMinerList();
        log.info("Timing task: start to cache all miner info to redis ...");
        StopWatch w = new StopWatch("Cache all miner info");
        w.start("fetch all miner sns");
        List<Miner> miners = minerMapper.findAll();
        w.stop();
        if (CollectionUtils.isEmpty(miners))
            return;
        w.start("fetch all white sns");
        // Set<String> whiteSet = whiteService.findAll(White.Type.Miner.getValue());
        w.stop();
        w.start("fetch all process data");
        Map<Long, Process> processMap = processService.findAll().stream().collect(Collectors.toMap(Process::getId,
                p -> p));
        w.stop();
        boolean isInUnneededDatetimeRange = isInUnneededDatetimeRange();
        w.start("process datas");
        Map<String, String> cache = new HashMap<>();
        for (Miner po : miners) {
            try {
                // if (null == po.getLeaveFactory()) continue; // has installed to machine
                MinerVo vo = new MinerVo(po);
                // boolean isWhite = whiteSet.contains(vo.getSn());
                Process p = vo.getPledgeProcessId() > 0
                        ? processMap.get(vo.getPledgeProcessId())
                        : null;
                processService.query(vo, whiteService.isWhiteMiner(vo.getSn()), p);
                if (!vo.isCanMining()) {
                    vo.setCanMining(isInUnneededDatetimeRange); // check freedom time quantum
                }
                if (vo.getStatus() == 0)
                    vo.setCanMining(false); // tag invalid machine
                String jsonString = JSONObject.toJSONString(vo);
                cache.put(vo.getSn(), jsonString);
            } catch (Exception e) {
                log.error("error occur when cache miner info[{}]", po.getSn());
            }
        }
        w.stop();
        w.start("put cache to redis");
        // jedisService.del(CacheKey.CACHEKEY_INFO_MINER_BY_SN);
        jedisService.hsetAll(CacheKey.CACHEKEY_INFO_MINER_BY_SN, cache);
        w.stop();
        log.info("Timing task: finish caching all miner info to redis ...");
        log.info(w.prettyPrint());
        log.info("=======================================================");
    }

    private boolean isInUnneededDatetimeRange() {
        if (StringUtils.isBlank(miningPledgeUnneededDatetimeRange))
            return false;
        String[] range = StringUtils.split(miningPledgeUnneededDatetimeRange, "-");
        long start = DateUtil.parse(range[0]).getTime();
        long end = DateUtil.parse(range[1]).getTime();
        long now = System.currentTimeMillis();
        boolean r = now > start && now < end;
        // log.info("Now is in unneeded datetime range:{}", r);
        return r;
    }

    @Scheduled(fixedDelay = 300000)
    public void cacheCpuModule() {
        log.info("Timing task: start to cache full cpu module to redis ...");
        List<CpuModel> ms = cpuModuleMapper.findAll();
        if (CollectionUtils.isEmpty(ms))
            return;
        Map<String, String> map = ms.stream()
                .collect(Collectors.toMap(CpuModel::getCpuModelName, m -> JSONObject.toJSONString(m)));
        jedisService.del(CacheKey.CACHEKEY_INFO_CPU_MODEL);
        jedisService.hsetAll(CacheKey.CACHEKEY_INFO_CPU_MODEL, map);
    }

    /**
     * Clean to refresh or correct the error cache information up per hour
     */
    @Scheduled(fixedDelay = 60000)
    @SchedulerLock(name = "MinerJobs.cleanWalletMinerList", lockAtLeastFor = 100, lockAtMostFor = 600000)
    public void cleanWalletMinerList() {
        log.info("Timing task: Start to flush all wallet miner list ...");
        jedisService.del(CacheKey.CACHEKEY_LIST_MINERS_BY_WALLET_ADDRES);
    }

    @Scheduled(fixedDelay = 600000)
    @SchedulerLock(name = "MinerJobs.cleanRules", lockAtLeastFor = 100, lockAtMostFor = 600000)
    public void cleanRules() {
        log.info("Timing task: Start to flush all rules ...");
        jedisService.del(CacheKey.CACHEKEY_ADMIN_RULE_BY_TYPE);
    }

}
