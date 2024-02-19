package com.synctech.statter.manager.api.config.filter;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.jedis.JedisService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component("EncryptCommunicationFilter")
public class EncryptCommunicationFilter implements Filter {

    @Autowired
    JedisService jedisService;

    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;

    @Value("${statter.encrypted-communication.enable}")
    Boolean encryptdCommunicationEnable;
    @Value("${statter.encrypted-communication.white-token.name}")
    String encryptdCommunicationWhiteTokenName;
    @Value("${statter.encrypted-communication.white-token.value}")
    String encryptdCommunicationWhiteTokenValue;
    @Value("${statter.encrypted-communication.token-name}")
    String encryptdCommunicationTokenName;
    @Value("${statter.encrypted-communication.token-pre-value}")
    String encryptdCommunicationTokenPrefixValue;
    @Value("${statter.encrypted-communication.token-pk}")
    String encryptdCommunicationTokenPrivateKey;

    RSA rsa;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("init api filter");
        if (this.encryptdCommunicationEnable) {
            if (StringUtils.isBlank(this.encryptdCommunicationTokenName)
                    || StringUtils.isBlank(this.encryptdCommunicationTokenPrefixValue)
                    || StringUtils.isBlank(this.encryptdCommunicationTokenPrivateKey)) {
                throw new ServletException(
                        "The encrypt communication module is open, but the token name nor the pk has not been setting correctly.");
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (this.encryptdCommunicationEnable)
            try {
                process((HttpServletRequest) request, (HttpServletResponse) response);
            } catch (AppBizException e) {
                handlerExceptionResolver.resolveException((HttpServletRequest) request, (HttpServletResponse) response,
                        null, e);
                return;
            }
        chain.doFilter(request, response);

    }

    private void process(HttpServletRequest req, HttpServletResponse resp) {
        if (StringUtils.isNotBlank(this.encryptdCommunicationWhiteTokenName)
                && StringUtils.isNotBlank(this.encryptdCommunicationWhiteTokenValue)) {
            String whiteToken = req.getHeader(this.encryptdCommunicationWhiteTokenName);
            if (StringUtils.equals(whiteToken, this.encryptdCommunicationWhiteTokenValue))
                return;
        }
        String t = req.getHeader(this.encryptdCommunicationTokenName);
        if (StringUtils.isBlank(t))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        if (null == this.rsa) {
            this.rsa = new RSA(this.encryptdCommunicationTokenPrivateKey, null);
        }
        byte[] decData = this.rsa.decrypt(t.getBytes(), KeyType.PrivateKey);
        String s = new String(decData);
        if (!StringUtils.startsWith(s, this.encryptdCommunicationTokenPrefixValue))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        String suffixStr = StringUtils.substringAfter(s, this.encryptdCommunicationTokenPrefixValue);
        if (!NumberUtil.isLong(suffixStr))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        long tt = NumberUtil.parseLong(suffixStr);
        long l = System.currentTimeMillis() - tt;
        if (l > 3600000) // if the timestamp in the client is diff from the server more than one hour,
                         // judging as illegal
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        else if (System.currentTimeMillis() - tt > 600000) // if the timestamp in the client is diff from the server
                                                           // more than ten m, judging as illegal
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
    }

    @Override
    public void destroy() {
        log.info("destroy api filter");
    }
}
