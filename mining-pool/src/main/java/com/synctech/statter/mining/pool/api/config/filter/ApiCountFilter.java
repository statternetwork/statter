package com.statter.statter.mining.pool.api.config.filter;

import com.statter.statter.base.entity.Promotion;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.redis.jedis.JedisService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component("ApiCountFilter")
public class ApiCountFilter implements Filter {

    @Autowired
    JedisService jedisService;

    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("init api count filter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("do api count filter");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        try {
            process(req, resp);
            chain.doFilter(request, response);
        } catch (AppBizException e) {
            resp.setHeader("Content-Type", "text/plain;charset=UTF-8");
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
