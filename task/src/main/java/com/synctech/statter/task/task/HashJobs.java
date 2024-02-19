package com.synctech.statter.task.task;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Miner;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.base.mapper.MinerMapper;
import com.synctech.statter.base.mapper.WalletMapper;
import com.synctech.statter.common.service.service.HashService;
import com.synctech.statter.common.service.vo.info.Hash;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

/**
 * jobs about hash
 */
@Component
@Slf4j
public class HashJobs {

    @Autowired
    JedisService jedisService;

    @Autowired
    HashService hashService;

    @Autowired
    MinerMapper minerMapper;

    @Autowired
    WalletMapper walletMapper;

    /**
     * Timing task:
     * - Analysis of computing power information (mineral computing power, mining
     * pool computing power)
     */
    @Scheduled(fixedDelay = 60000)
    @SchedulerLock(name = "analyzeHashRate", lockAtLeastFor = 100, lockAtMostFor = 60000)
    public void analyzeHashRate() {
        log.info("Timing task: : Start to analyze hash rate ...");
        Jedis j = jedisService.get();
        try {
            String ct = j.get(CacheKey.CACHEKEY_HASH_INFO_GLOBAL_CACHE_TIME);
            long ctl = 0;
            if (!StringUtils.isBlank(ct))
                ctl = NumberUtil.parseLong(ct);
            if ((System.currentTimeMillis() - ctl) < 60000) {// The computing power update time is less than one minute,
                                                             // skip
                return;
            }
            analyzeHashRateImpl(j);
        } finally {
            jedisService.flush(j);
        }
    }

    private void analyzeHashRateImpl(Jedis j) {
        List<Miner> ms = minerMapper.findAll();
        if (CollectionUtils.isEmpty(ms)) {
            log.warn("Timing task -- Analysis computing power -- end: no miner data");
            j.del(CacheKey.CACHEKEY_HASH_INFO_MINER);
            return;
        }
        List<Wallet> ws = walletMapper.findAll();
        if (CollectionUtils.isEmpty(ws)) {
            log.warn("Timing task -- Analysis computing power -- end: no wallet data");
            j.del(CacheKey.CACHEKEY_HASH_INFO_MINER);
            return;
        }
        Map<String, String> all = j.hgetAll(CacheKey.CACHEKEY_HASH_INFO_MINER);
        if (CollectionUtils.isEmpty(all)) {
            log.warn("Timing task -- Analysis computing power -- end: no miner hash data");
            return;
        }
        // analyze wallet hash
        j.del(CacheKey.CACHEKEY_HASH_INFO_WALLET);
        for (Miner m : ms) {
            long h = 0;
            if (StringUtils.isBlank(m.getWalletAddress()))
                continue;
            String v1 = j.hget(CacheKey.CACHEKEY_HASH_INFO_WALLET, m.getWalletAddress());// the key is wallet address
            if (!StringUtils.isBlank(v1))
                h = NumberUtil.parseLong(v1);
            String v2 = all.get(m.getSn());
            if (StringUtils.isBlank(v2))
                continue;
            Hash hash = JSONObject.parseObject(v2, Hash.class);
            if ((System.currentTimeMillis() - hash.getT()) > 120000) {// expired data
                j.hdel(CacheKey.CACHEKEY_HASH_INFO_MINER, m.getSn());
                continue;
            }
            h += hash.getH();
            j.hset(CacheKey.CACHEKEY_HASH_INFO_WALLET, m.getWalletAddress(), h + "");
        }
        // analyze pool hash
        j.del(CacheKey.CACHEKEY_HASH_INFO_PROMOTION);
        Map<String, String> whs = j.hgetAll(CacheKey.CACHEKEY_HASH_INFO_WALLET);// the key is wallet address
        for (Wallet w : ws) {
            if (StringUtils.isBlank(w.getPromotionAddress()))
                continue;
            long h = 0;
            String v1 = j.hget(CacheKey.CACHEKEY_HASH_INFO_PROMOTION, w.getPromotionAddress());// the key is promotion
                                                                                               // address
            if (!StringUtils.isBlank(v1))
                h = NumberUtil.parseLong(v1);
            String v2 = whs.get(w.getAddress());
            if (StringUtils.isBlank(v2))
                continue;
            h += NumberUtil.parseLong(v2);
            j.hset(CacheKey.CACHEKEY_HASH_INFO_PROMOTION, w.getPromotionAddress(), h + "");
        }
        // global hash info
        long globalHash = 0;
        Map<String, String> phs = j.hgetAll(CacheKey.CACHEKEY_HASH_INFO_PROMOTION);// the key is promotion address
        for (Map.Entry<String, String> en : phs.entrySet()) {
            globalHash += NumberUtil.parseLong(en.getValue());
        }
        j.set(CacheKey.CACHEKEY_HASH_INFO_GLOBAL, globalHash + "");
        j.set(CacheKey.CACHEKEY_HASH_INFO_GLOBAL_CACHE_TIME, System.currentTimeMillis() + "");// refresh update time
    }

}
