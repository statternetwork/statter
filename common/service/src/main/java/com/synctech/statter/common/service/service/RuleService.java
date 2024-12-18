package com.statter.statter.common.service.service;

import com.alibaba.fastjson.JSONObject;
import com.statter.statter.base.entity.Rule;
import com.statter.statter.base.mapper.RuleMapper;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.redis.config.vo.Hget;
import com.statter.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RuleService {

    @Autowired
    JedisService jedisService;

    @Autowired
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
                p -> {
                    Rule rule = ruleMapper.findByType(t);
                    return null != rule ? rule.getContent() : null;
                });
        if (StringUtils.isBlank(r))
            throw new AppBizException(HttpStatusExtend.ERROR_RULE_NOT_FOUND, t + "");
        return JSONObject.parseObject(r);
    }

    public List<Rule> findAll() {
        return ruleMapper.findAll();
    }

}
