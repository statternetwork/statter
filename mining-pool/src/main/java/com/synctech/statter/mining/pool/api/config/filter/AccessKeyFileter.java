package com.synctech.statter.mining.pool.api.config.filter;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component("AccessKeyFileter")
public class AccessKeyFileter implements Filter {

    @Autowired
    JedisService jedisService;

    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("init api filter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        try {
            log.info("do api filter:{}", req.getRequestURI());
            process(req, resp);
            chain.doFilter(request, response);
        } catch (AppBizException e) {
            handlerExceptionResolver.resolveException(req, resp, null, e);
        }
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) {
        String ak = req.getHeader("SAK");
        if (!StringUtils.hasText(ak)) {
            ak = req.getHeader("sak");
            if (!StringUtils.hasText(ak))
                throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_INVALID_ACCESS_KEY);
        }
        Promotion p = jedisService.hget(CacheKey.CACHEKEY_AK_PROMOTION_BY_AK, ak, Promotion.class);
        if (null == p) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_INVALID_ACCESS_KEY);
        }
        JSONObject j = jedisService.hget(CacheKey.CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS, p.getAddress(),
                JSONObject.class);
        if (j.getLongValue("etl") < System.currentTimeMillis()) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_INVALID_ACCESS_KEY);
        }
        req.setAttribute("PROMOTION", p); // use for this request
    }

    @Override
    public void destroy() {
        log.info("destroy api filter");
    }
}
