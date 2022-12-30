package com.synctech.statter.common.service.service;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.base.entity.White;
import com.synctech.statter.base.mapper.WhiteMapper;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class WhiteService {

    @Resource
    JedisService jedisService;

    @Resource
    WhiteMapper whiteMapper;

    @Resource
    RuleService ruleService;

    @Transactional
    public void add(String n, White.Type t) {
        White w = new White().setN(n).setT(t.getValue());
        whiteMapper.add(w);
    }

    public boolean isWhiteModelOpen() {
        JSONObject j = null;
        try {
            j = ruleService.get(Rule.Type.WhiteList);
            if (null == j) return false;
        } catch (Exception e) {
            log.warn("white list rule has not be setting");
            return false;
        }
        return j.getBoolean("minerWhiteListSwitch");
    }

    public boolean isWhiteMiner(String n) {
        if (!this.isWhiteModelOpen()) return false;
        return this.isWhiteMinerImpl(n);
    }

    public boolean isWhiteMinerImpl(String n) {
        Boolean exist = jedisService.hget(CacheKey.CACHEKEY_ADMIN_WHITE_LIST_MINER, n, Boolean.class);
        if (null == exist) {
            int we = whiteMapper.exist(n, White.Type.Miner.getValue());
            exist = we > 0 ? true : false;
            jedisService.hset(CacheKey.CACHEKEY_ADMIN_WHITE_LIST_MINER, n, exist);
        }
        return exist;
    }

}
