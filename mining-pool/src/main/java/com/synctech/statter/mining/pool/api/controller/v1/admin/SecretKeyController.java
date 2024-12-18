package com.statter.statter.mining.pool.api.controller.v1.admin;

import cn.hutool.json.JSONObject;
import com.statter.statter.base.entity.Promotion;
import com.statter.statter.common.service.service.PromotionService;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.constant.restful.DataResponse;
import com.statter.statter.mining.pool.api.controller.v1.CommonController;
import com.statter.statter.mining.pool.api.controller.v1.admin.vo.RefreshSecretKey;
import com.statter.statter.redis.jedis.JedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "mining pool: secret key")
@RequestMapping("statter/mining/pool/api/v1/sk")
@RestController()
public class SecretKeyController extends CommonController {

    @Autowired
    JedisService jedisService;

    @Autowired
    PromotionService promotionService;

    @Operation(method = "POST", description = "Refresh secret key by the management key.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefreshSecretKey.Req.class))), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))))
    @PostMapping("refresh")
    public String refresh(@RequestBody RefreshSecretKey.Req req) {
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
