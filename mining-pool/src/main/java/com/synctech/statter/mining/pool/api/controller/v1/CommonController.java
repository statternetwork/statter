package com.synctech.statter.mining.pool.api.controller.v1;

import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.common.service.service.HashService;
import com.synctech.statter.common.service.service.PromotionService;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.jedis.JedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class CommonController {

    @Autowired
    protected JedisService jedisService;

    @Autowired
    protected HashService hashService;

    @Autowired
    protected PromotionService promotionService;

    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    protected Promotion getPromotion() {
        Object o = getRequest().getAttribute("PROMOTION");
        if (null == o)
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        return (Promotion) o;
    }

    protected String getPromotionAddress() {
        return getPromotion().getAddress();
    }

}
