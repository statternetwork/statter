package com.synctech.statter.common.service.service;

import com.synctech.statter.base.mapper.MinerMapper;
import com.synctech.statter.common.service.vo.info.Hash;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class HashService {

    @Autowired
    JedisService jedisService;

    @Autowired
    MinerMapper minerMapper;

    /**
     * <p>update the real-time hash of the miner</p>
     *
     * @param sn
     * @param h
     * @return
     */
    public void update(String sn, long h) {
        Hash t = new Hash().setH(h).setT(System.currentTimeMillis());
        log.debug("update miner hash: {} = {}", sn, h);
        jedisService.hset(CacheKey.CACHEKEY_HASH_INFO_MINER, sn, t);
    }

    public void updateMaxHash(String sn, long maxHistoryHash) {
        // log.info("updateMaxHash miner hash: {} = {}", sn, maxHistoryHash);
        minerMapper.updateMaxHash(sn, maxHistoryHash);
    }

    /**
     * <p>get the real-time hash of the miner</p>
     *
     * @param sn
     * @return
     */
    public Hash get(String sn) {
        Hash h = jedisService.hget(CacheKey.CACHEKEY_HASH_INFO_MINER, sn, Hash.class);
        return null == h ? new Hash() : h;
    }

    /**
     * <p>get the real-time hash of all miner</p>
     *
     * @return
     */
    public Map<String, String> getAll() {
        return jedisService.hgetAll(CacheKey.CACHEKEY_HASH_INFO_MINER);
    }

    public Map<String, String> getAllWallet() {
        return jedisService.hgetAll(CacheKey.CACHEKEY_HASH_INFO_WALLET);
    }

    public Map<String, String> getAllPromotion() {
        return jedisService.hgetAll(CacheKey.CACHEKEY_HASH_INFO_PROMOTION);
    }

    /**
     * <p>get total hash</p>
     *
     * @return
     */
    public String getTotal() {
        return jedisService.get(CacheKey.CACHEKEY_HASH_INFO_GLOBAL);
    }

    /**
     * query miner(replenish the hash info)
     *
     * @param m
     */
    public void queryMiner(MinerVo m) {
        Hash hash = get(m.getSn());
        if (null == hash) {
            m.setHash(0);
            m.setOnline(false);
            return;
        }
        boolean isOnline = hash.isOnline();
        m.setHash(isOnline ? hash.getH() : 0);
        m.setOnline(isOnline);
    }

}
