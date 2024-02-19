package com.synctech.statter.mining.pool.api.controller.v1.admin;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.mining.pool.api.controller.v1.CommonController;
import com.synctech.statter.mining.pool.api.controller.v1.admin.vo.GetAccessKey;
import com.synctech.statter.redis.jedis.JedisService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

@Slf4j
@Api(value = "api of token")
@RequestMapping("v1/ak")
@RestController()
public class AccessKeyController extends CommonController {

    @Autowired
    JedisService jedisService;
    @Value("${statter.promotion.ak.expire.ms:86400000}")
    private Long akExpireMillionSecond;

    @ApiOperation(httpMethod = "POST", value = "Obtain a new access key which is used for ask other pool api interface, "
            +
            "the ak will be expired after 2 hours.if the expire time is less than 30 minutes, you can refresh it again."
            +
            "if malicious refresh ak outside the rules, the ak and the promotion will be frozen several days.")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = GetAccessKey.Resp.class) })
    @PostMapping("refresh")
    public String refresh(@ApiParam(type = "json", required = true) @RequestBody GetAccessKey.Req req) {
        if (null == req || StringUtils.isBlank(req.getA()) || StringUtils.isBlank(req.getSk()))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        JSONObject info = jedisService.hget(CacheKey.CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS, req.getA(),
                JSONObject.class);// check refresh time
        if (null != info) {
            if (info.getIntValue("c") > 24) {// malicious refresh time is greater than 24, it will freeze the promotion
                throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_PROMOTION_IS_FROZEN);
                // } else if (System.currentTimeMillis() - info.getLongValue("et") > -1800000) {
                // info.put("c", info.getIntValue("c") + 1);
                // jedisService.hset(CacheKey.CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS,
                // req.getA(), info.toJSONString());
                // throw new
                // AppBizException(HttpStatusExtend.ERROR_POOL_API_MALICIOUS_REFRESH_AK);
            }
        }
        Promotion p = jedisService.hget(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, req.getA(), Promotion.class);
        if (null == p)
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        else if (!StringUtils.equals(req.getSk(), p.getSecretKey()))
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_WRONG_SECRET_KEY);
        String newToken = req.getA() + System.currentTimeMillis() + RandomUtil.randomString(32) + req.getSk();
        newToken = MD5.create().digestHex(newToken);
        long n = System.currentTimeMillis();
        info = new JSONObject();
        info.put("c", 0);
        info.put("ct", new Date(n).toString());
        info.put("ctl", n);
        long expire = n + akExpireMillionSecond;
        info.put("et", new Date(expire).toString());
        info.put("etl", expire);
        info.put("ak", newToken);
        jedisService.hset(CacheKey.CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS, req.getA(), info);
        jedisService.hset(CacheKey.CACHEKEY_AK_PROMOTION_BY_AK, newToken, p);
        getResponse().setHeader("sak", newToken);
        return DataResponse.success(info.toJavaObject(GetAccessKey.Resp.class));
    }

}
