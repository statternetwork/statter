package com.synctech.statter.common.service.service;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.base.mapper.RuleMapper;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.config.vo.Hget;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class RuleService {

    @Resource
    JedisService jedisService;

    @Resource
    RuleMapper ruleMapper;

    @Transactional
    public void add(byte t, JSONObject j) {
        ruleMapper.deleteByType(t);
        Rule r = new Rule();
        r.setType(t);
        r.setContent(j.toJSONString());
        ruleMapper.add(r);
        jedisService.hdel(CacheKey.CACHEKEY_ADMIN_RULE_BY_TYPE, t + "");
        getImpl(t);
    }

    public JSONObject get(Rule.Type t) {
        return getImpl(t.getValue());
    }

    JSONObject getImpl(byte t) {
        String r = jedisService.hget(new Hget<>(CacheKey.CACHEKEY_ADMIN_RULE_BY_TYPE, t + "", CacheKey.CACHEKEY_ADMIN_RULE_BY_TYPE_LOCK, String.class),
                p -> ruleMapper.findByType(t).getContent());
        if (StringUtils.isBlank(r))
            throw new AppBizException(HttpStatusExtend.ERROR_RULE_NOT_FOUND);
        return JSONObject.parseObject(r);
    }

    public List<Rule> findAll() {
        return ruleMapper.findAll();
    }

}
