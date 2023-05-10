package com.synctech.statter.ledger.api.service;


import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Ledger;
import com.synctech.statter.base.mapper.LedgerMapper;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class LedgerService {

    @Autowired
    JedisService jedisService;

    @Autowired
    LedgerMapper ledgerMapper;

    private String getBlockLedgerKey(long blockIndex) {
        return CacheKey.CACHEKEY_MINING_LEDGER_KEY_PREFIX + blockIndex;
    }

    /**
     * book the ledge
     *
     * @param blockIndex
     * @param sn
     * @param count
     * @return
     */
    public long count(long blockIndex, String sn, long count) {
        String k = getBlockLedgerKey(blockIndex);
        String lk = CacheKey.CACHEKEY_MINING_LEDGER_LOCK + sn;
        Jedis j = jedisService.get();
        jedisService.lock(lk, 30, j);
        try {
            long c = 0;
            if (j.hexists(k, sn)) {// exist the cache, append the workload to the ledge
                String v = j.hget(k, sn);
                count += NumberUtil.parseLong(v);
            }
            long r = j.hset(k, sn, count + "");
            return count;
        } finally {
            jedisService.unlock(lk, j);
            jedisService.flush(j);
        }
    }

    /**
     * storage the redis ledge to db.
     * At a block height, there will be multiple real-time ledger,
     * but don't worry, the gateway will confirm which one is valid as a quote
     *
     * @param bi
     * @param sn
     * @param wa
     * @return
     */
    public LedgerExractDto extract(long bi, String sn, String wa, String pa) {
        Jedis j = jedisService.get();
        jedisService.lock(CacheKey.CACHEKEY_MINING_LEDGER_LOCK + sn, 30, j);
        try {
            String k = getBlockLedgerKey(bi);
            if (!j.exists(k)) {// wrong block index
                throw new AppBizException(HttpStatusExtend.ERROR_MINING_REPORT_LEDGER_NOT_FOUND_BY_BLOCKINDEX);
            }
            Map<String, String> v = j.hgetAll(k);
            log.debug("extract = {}", v);
            JSONObject json = new JSONObject();
            json.putAll(v);
            // storage the redis ledge to db
            Ledger l = new Ledger();
            l.setId(UUID.randomUUID().toString());
            l.setBlockIndex(bi);
            l.setState(Ledger.State.Storage.getValue());
            l.setSn(sn);
            l.setAddress(wa);
            l.setPromotionAddress(pa);
            l.setData(json.toJSONString().getBytes());
            ledgerMapper.add(l);
            LedgerExractDto dto = new LedgerExractDto();
            dto.setId(l.getId());
            dto.setDataMap(v);
            return dto;
        } finally {
            jedisService.unlock(CacheKey.CACHEKEY_MINING_LEDGER_LOCK + sn, j);
            jedisService.flush(j);
        }
    }

    @Data
    @Accessors(chain = true)
    public class LedgerExractDto extends Ledger{
        Map<String, String> dataMap;
    }

    public Ledger get(String id) {
        return ledgerMapper.findOne(id);
    }

}
