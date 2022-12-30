package com.synctech.statter.mining.pool.api.config.filter;

import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component("ApiCountFileter")
public class ApiCountFileter implements Filter {

    @Resource
    JedisService jedisService;

    @Resource
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("init api count filter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("do api count filter");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        try {
            process(req, resp);
            chain.doFilter(request, response);
        } catch (AppBizException e) {
            resp.setHeader("Content-Type","text/plain;charset=UTF-8");
            handlerExceptionResolver.resolveException(req, resp, null, e);
        }
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) {
        Promotion p = (Promotion) req.getAttribute("PROMOTION");
    }

    @Override
    public void destroy() {
        log.info("destroy api filter");
    }
}
