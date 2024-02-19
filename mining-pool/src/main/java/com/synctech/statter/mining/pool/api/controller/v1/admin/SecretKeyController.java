package com.synctech.statter.mining.pool.api.controller.v1.admin;

import cn.hutool.json.JSONObject;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.common.service.service.PromotionService;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.mining.pool.api.controller.v1.CommonController;
import com.synctech.statter.mining.pool.api.controller.v1.admin.vo.RefreshSecretKey;
import com.synctech.statter.redis.jedis.JedisService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Api(value = "api of manage secret key")
@RequestMapping("v1/sk")
@RestController()
public class SecretKeyController extends CommonController {

    @Autowired
    JedisService jedisService;

    @Autowired
    PromotionService promotionService;

    @ApiOperation(httpMethod = "POST", value = "Refresh secret key by the management key.")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
    @PostMapping("refresh")
    public String refresh(@ApiParam(type = "json", required = true) @RequestBody RefreshSecretKey.Req req) {
        if (null == req || StringUtils.isBlank(req.getA()) || StringUtils.isBlank(req.getMk()))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        Promotion p = jedisService.hget(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, req.getA(), Promotion.class);
        if (null == p)
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        if (!StringUtils.equals(p.getManagementKey(), req.getMk())) {
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        } else if (StringUtils.isNotBlank(p.getSecretKey())
                && (System.currentTimeMillis() - p.getSecretKeyUptTime().getTime() < 86400000)) {
            return DataResponse.success(p.getSecretKey());
        }
        promotionService.refreshSecretKey(req.getA());
        p = promotionService.get(req.getA());
        jedisService.hset(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, req.getA(), new JSONObject(p));
        return DataResponse.success(p.getSecretKey());
    }

}
