package com.statter.statter.task.task;

import com.statter.statter.base.mapper.MinerMapper;
import com.statter.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;

@Component
@Slf4j
public class WalletJobs {

    @Autowired
    JedisService jedisService;

    @Autowired
    MinerMapper minerMapper;

}
