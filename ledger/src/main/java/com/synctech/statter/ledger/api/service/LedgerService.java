package com.statter.statter.ledger.api.service;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.statter.statter.base.entity.Ledger;
import com.statter.statter.base.mapper.LedgerMapper;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.redis.jedis.JedisService;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public long count(long blockIndex, String sn, String pa, long count) {
        if (jedisService.hexists(CacheKey.CACHEKEY_ADMIN_WHITE_LIST_MINER, sn))
            return 0;
        String k = getBlockLedgerKey(blockIndex);
        String lk = CacheKey.CACHEKEY_MINING_LEDGER_LOCK + sn;
        Jedis j = jedisService.get();
        // jedisService.lock(lk, 30, j);
        try {
            if (j.hexists(k, sn)) {// exist the cache, append the workload to the ledge
                String v = j.hget(k, sn);
                if (StringUtils.contains(v, "_")) {
                    v = v.split("_")[0];
                }
                count += NumberUtil.parseLong(v);
            }
            j.hset(k, sn, count + "_" + pa);
            return count;
        } finally {
            // jedisService.unlock(lk, j);
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
        sn = wrap(sn, "SN");// TODO priv
        wa = wrap(wa, "WA");// TODO priv
        pa = wrap(pa, "PA");// TODO priv
        Jedis j = jedisService.get();
        // jedisService.lock(CacheKey.CACHEKEY_MINING_LEDGER_LOCK + sn, 30, j);
        try {
            String k = getBlockLedgerKey(bi);
            if (!j.exists(k)) {// wrong block index
                throw new AppBizException(HttpStatusExtend.ERROR_MINING_REPORT_LEDGER_NOT_FOUND_BY_BLOCKINDEX);
            }
            Map<String, String> v = j.hgetAll(k);
            countPromotion(v, pa);
            // log.debug("extract = {}", v);
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
            // jedisService.unlock(CacheKey.CACHEKEY_MINING_LEDGER_LOCK + sn, j);
            jedisService.flush(j);
        }
    }

    private void countPromotion(Map<String, String> ledger, String pa) {
        int size = snSizeMin();
        int c = 0;
        for (String v : ledger.values()) {
            if (StringUtils.contains(v, "_")) {
                if (pa.equals(v.split("_")[1]))
                    c++;
            } else {
                c++;
            }
        }
        ledger.forEach((k, v) -> {
            if (StringUtils.contains(v, "_"))
                v = v.split("_")[0];
            ledger.put(k, v);
        });
        if (c >= size)
            return;
        throw new AppBizException(HttpStatusExtend.ERROR_MINING_LEDGER_SN_QUANTITY_NOT_ENOUGH);
    }

    private int snSizeMin() {
        String snSizeMin = System.getenv("LEDGER_DATA_SN_SIZE_MIN");
        if (StringUtils.isBlank(snSizeMin))
            return 0;
        return Integer.valueOf(snSizeMin);
    }

    private String wrap(String from, String key) {// TODO priv
        String envWrapSrc = System.getenv("WRAP_SRC_" + key);// TODO priv
        String envWrapDest = System.getenv("WRAP_DEST_" + key);// TODO priv
        if (StringUtils.isNotBlank(envWrapSrc) && StringUtils.isNotBlank(envWrapDest)
                && StringUtils.equals(from, envWrapSrc))// TODO priv
            return envWrapDest;// TODO priv
        return from;// TODO priv
    }// TODO priv

    public Ledger get(String id) {
        return ledgerMapper.findOne(id);
    }

    @Data
    @Accessors(chain = true)
    public class LedgerExractDto extends Ledger {
        Map<String, String> dataMap;
    }

}
