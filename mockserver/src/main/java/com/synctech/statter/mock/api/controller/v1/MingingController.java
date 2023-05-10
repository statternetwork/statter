package com.synctech.statter.mock.api.controller.v1;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.mock.api.vo.*;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RequestMapping("gateway")
@RestController
public class MingingController {

    static String CACHEKEY_MOCK_BLOCK_INDEX = "CACHEKEY_MOCK_BLOCK_INDEX";
    static String CACHEKEY_MOCK_TASK = "CACHEKEY_MOCK_TASK";
    @Autowired
    JedisService jedisService;
    @Value("${statter.task.fixed-load:000000}")
    String statterTaskFixedLoad;

    private PoolTask genPoolTask(long bi) {
        PoolTask pt = new PoolTask();
        pt.setWorkload(this.statterTaskFixedLoad);
        pt.setStatus(0);
        pt.setBlockHash(MD5.create().digestHex(RandomUtil.randomLong() + ""));
        Block b = new Block();
        b.setBlockIndex(bi + "");
        b.setPath("/ws/block0/" + bi + ".bnk");
        b.setOnMingChain(1);
        b.setHeadHash(MD5.create().digestHex(RandomUtil.randomLong() + ""));
        b.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        pt.setBlock(b);
        return pt;
    }

    private long getBlockIndexImpl() {
        String v = jedisService.get(CACHEKEY_MOCK_BLOCK_INDEX);
        if (StringUtils.isBlank(v)) {
            v = "1";
            jedisService.set(CACHEKEY_MOCK_BLOCK_INDEX, "1");
        }
        long bi = NumberUtil.parseLong(v);
        return bi;
    }

    @ResponseBody
    @PostMapping("getBlockIndex")
    public String getBlockIndex() {
        long bi = getBlockIndexImpl();
        JSONObject r = new JSONObject();
        r.put("code", "0");
        r.put("msg", "");
        JSONObject js = new JSONObject();
        js.put("state", 0);
        js.put("blockIndex", bi);
        r.put("content", js);
        return r.toJSONString();
    }

    @ResponseBody
    @PostMapping("accept")
    public String accept(@RequestBody() PoolTaskInfoDataReq body) {
        long bi = getBlockIndexImpl();
        String v = jedisService.hget(CACHEKEY_MOCK_TASK, "" + bi);
        PoolTask pt = null;
        if (StringUtils.isBlank(v)) {
            pt = genPoolTask(bi);
            jedisService.hset(CACHEKEY_MOCK_TASK, "" + bi, JSONObject.toJSONString(pt));
        } else {
            pt = JSONObject.parseObject(v, PoolTask.class);
        }
        PoolTaskInfoDataResp r = new PoolTaskInfoDataResp();
        r.setCode("0");
        r.setMsg("");
        r.setContent(pt);
        return JSONObject.toJSONString(r);
    }

    @ResponseBody
    @PostMapping("result")
    public String result(@RequestBody MiningReportReq req) {
        log.debug("result body = {}", JSONObject.toJSONString(req));
        JSONObject r = new JSONObject();
        r.put("code", "0");
        r.put("msg", "");
        r.put("content", "");
        if (req.getBlockIndex() != getBlockIndexImpl()) {
            r.put("code", "-1");
            r.put("msg", "wrong tast");
            return r.toJSONString();
        }
        long bi = getBlockIndexImpl();
        jedisService.set(CACHEKEY_MOCK_BLOCK_INDEX, "" + (bi + 1));
        return r.toJSONString();
    }


}
