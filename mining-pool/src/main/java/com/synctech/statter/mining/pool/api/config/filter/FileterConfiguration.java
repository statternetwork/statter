package com.synctech.statter.mining.pool.api.config.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class FileterConfiguration {

    static String[] patterns = new String[]{
            "/v1/prom/*",
            "/v1/miner/*",
            "/v1/ledger/*",
            "/v1/wallet/list/*",
    };

    @Resource
    @Bean("FilterRegistrationBeanAccessKeyFileter")
    public FilterRegistrationBean<AccessKeyFileter> registeAccessKeyFileter(AccessKeyFileter f) {
        FilterRegistrationBean<AccessKeyFileter> reg = new FilterRegistrationBean();
        reg.setFilter(f);
        reg.addUrlPatterns(patterns);
        reg.setOrder(2);
        reg.setAsyncSupported(true);
        return reg;
    }

    @Resource
    @Bean("FilterRegistrationBeanApiCountFileter")
    public FilterRegistrationBean<ApiCountFileter> registeApiCountFileter(ApiCountFileter f) {
        FilterRegistrationBean<ApiCountFileter> reg = new FilterRegistrationBean();
        reg.setFilter(f);
        reg.addUrlPatterns(patterns);
        reg.setOrder(3);
        reg.setAsyncSupported(true);
        return reg;
    }


}
