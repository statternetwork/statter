package com.synctech.statter.mining.pool.api.config.filter;

import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.jedis.JedisService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component("AccessKeyFilter")
public class AccessKeyFilter implements Filter {

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
            String ak = req.getHeader("SAK");
            log.info("do api filter [{}] :{}", StringUtils.hasText(ak) ? ak : "-", req.getRequestURI());
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
                throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_EMPTY_ACCESS_KEY, false);
        }
        String akInfo = jedisService.hget(CacheKey.CACHEKEY_AK, ak);
        if (!StringUtils.hasText(akInfo)) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_INVALID_ACCESS_KEY);
        }
        String[] ss = akInfo.split("::");
        long expire = Long.parseLong(ss[0]);
        if (expire < System.currentTimeMillis()) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_ACCESS_KEY_EXPIRED);
        }
        Promotion p = jedisService.hget(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, ss[1], Promotion.class);
        req.setAttribute("PROMOTION", p); // use for this request
    }

    @Override
    public void destroy() {
        log.info("destroy api filter");
    }
}
