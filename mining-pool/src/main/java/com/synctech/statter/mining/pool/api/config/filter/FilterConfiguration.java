package com.statter.statter.mining.pool.api.config.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    static String[] patterns = new String[] {
            "/statter/mining/pool/api/v1/prom/*",
            "/statter/mining/pool/api/v1/miner/*",
            "/statter/mining/pool/api/v1/ledger/*",
            "/statter/mining/pool/api/v1/wallet/list/*",
    };

    @Autowired
    @Bean("FilterRegistrationBeanAccessKeyFilter")
    public FilterRegistrationBean<AccessKeyFilter> regAccessKeyFilter(AccessKeyFilter f) {
        FilterRegistrationBean<AccessKeyFilter> reg = new FilterRegistrationBean();
        reg.setFilter(f);
        reg.addUrlPatterns(patterns);
        reg.setOrder(2);
        reg.setAsyncSupported(true);
        return reg;
    }

    @Autowired
    @Bean("FilterRegistrationBeanApiCountFilter")
    public FilterRegistrationBean<ApiCountFilter> regApiCountFilter(ApiCountFilter f) {
        FilterRegistrationBean<ApiCountFilter> reg = new FilterRegistrationBean();
        reg.setFilter(f);
        reg.addUrlPatterns(patterns);
        reg.setOrder(3);
        reg.setAsyncSupported(true);
        return reg;
    }

}
