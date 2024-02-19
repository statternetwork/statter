package com.synctech.statter.mining.pool.api.controller.v1.admin;

import cn.hutool.core.date.DateUtil;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@Tag(name = "mining pool: token")
@RequestMapping("statter/mining/pool/api/v1/ak")
@RestController()
public class AccessKeyController extends CommonController {

    @Autowired
    JedisService jedisService;

    @Value("${statter.promotion.ak.expire.ms:86400000}")
    private Long akExpireMillionSecond;
    // malicious refresh times is greater than 24, it will freeze the promotion
    @Value("${statter.promotion.ak.refresh.max.times:100}")
    private Integer promotionAkRefreshMaxTimes;

    @Operation(method = "POST", description = "Obtain a new access key which is used for ask other pool api interface, "
            + "the ak will be expired after 2 hours.if the expire time is less than 30 minutes, you can refresh it again."
            + "if malicious refresh ak outside the rules, the ak and the promotion will be frozen several days.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAccessKey.Req.class))), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAccessKey.Resp.class))))
    @PostMapping("refresh")
    public String refresh(@RequestBody GetAccessKey.Req req) {
        if (null == req || StringUtils.isBlank(req.getA()) || StringUtils.isBlank(req.getSk()))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        String pa = req.getA();
        long n = System.currentTimeMillis();
        long expire = n + akExpireMillionSecond;
        String curDateStr = DateUtil.format(new Date(n), "yyyyMMdd");
        JSONObject info = jedisService.hget(CacheKey.CACHEKEY_AK_ASK_INFO, pa, JSONObject.class);// check refresh time
        int c = 0;
        if (null != info) {
            c = info.getIntValue("c");
            if (StringUtils.equals(curDateStr, info.getString("ct"))) { // one day
                if (c >= promotionAkRefreshMaxTimes) // max time check
                    throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_PROMOTION_IS_FROZEN);
            } else {
                c = 0; // reset refresh times
            }
        }
        Promotion p = jedisService.hget(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, pa, Promotion.class);
        if (null == p)
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        else if (!StringUtils.equals(req.getSk(), p.getSecretKey()))
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_WRONG_SECRET_KEY);
        String newToken = pa + System.currentTimeMillis() + RandomUtil.randomString(32) + req.getSk();
        newToken = MD5.create().digestHex(newToken);
        info = new JSONObject();
        info.put("c", c + 1);
        info.put("ct", curDateStr);// create date
        info.put("ctl", n);// create datetime
        // info.put("et", new Date(expire).toString());
        info.put("etl", expire);
        info.put("ak", newToken);
        jedisService.hset(CacheKey.CACHEKEY_AK_ASK_INFO, pa, info);
        jedisService.hset(CacheKey.CACHEKEY_AK, newToken, expire + "::" + p.getAddress());
        getResponse().setHeader("sak", newToken);
        return DataResponse.success(info.toJavaObject(GetAccessKey.Resp.class));
    }

}
