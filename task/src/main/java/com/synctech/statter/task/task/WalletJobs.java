package com.synctech.statter.task.task;

import com.synctech.statter.base.mapper.MinerMapper;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class WalletJobs {

    @Autowired
    JedisService jedisService;

    @Resource
    MinerMapper minerMapper;

}
