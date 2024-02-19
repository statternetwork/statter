package com.synctech.statter.common.service.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.base.entity.White;
import com.synctech.statter.base.mapper.WhiteMapper;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WhiteService {

    @Autowired
    JedisService jedisService;

    @Autowired
    WhiteMapper whiteMapper;

//    @Autowired
//    RuleService ruleService;

    @Transactional
    public void add(String n, White.Type t) {
        White w = new White().setN(n).setT(t.getValue());
        whiteMapper.add(w);
    }

//    public Set<String> findAll(byte type) {
//        List<White> list = whiteMapper.findByType(type);
//        return CollectionUtil.isEmpty(list) ? new HashSet<>() : list.stream().map(w -> w.getN()).collect(Collectors.toSet());
//    }

//    public boolean isWhiteModelOpen() {
//        JSONObject j = null;
//        try {
//            j = ruleService.get(Rule.Type.WhiteList);
//            if (null == j) return false;
//        } catch (Exception e) {
//            log.warn("white list rule has not be setting");
//            return false;
//        }
//        return j.getBoolean("minerWhiteListSwitch");
//    }

    public boolean isWhiteMiner(String n) {
        return jedisService.hexists(CacheKey.CACHEKEY_ADMIN_WHITE_LIST_MINER, n);
//        int exist = whiteMapper.exist(n, White.Type.Miner.getValue());
//        return exist == 1;
    }

    public void cacheWhiteMinerList() {
        List<White> list = whiteMapper.findByType((byte) 1);
        jedisService.del(CacheKey.CACHEKEY_ADMIN_WHITE_LIST_MINER);
        if (CollectionUtils.isEmpty(list)) return;
        Map<String, String> map = list.stream().collect(Collectors.toMap(White::getN, w -> "1"));
        jedisService.hsetAll(CacheKey.CACHEKEY_ADMIN_WHITE_LIST_MINER, map);
//        for (int i = 0; i < list.size(); i++) {
//            jedisService.hset(CacheKey.CACHEKEY_ADMIN_WHITE_LIST_MINER, list.get(i).getN(), true);
//        }
    }

}
