package com.synctech.statter.manager.api.config.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class FileterConfiguration {

    @Resource
    @Bean("FilterRegistrationBeanEncryptdCommunicationFileter")
    public FilterRegistrationBean<EncryptdCommunicationFileter> registeAccessKeyFileter(EncryptdCommunicationFileter f) {
        FilterRegistrationBean<EncryptdCommunicationFileter> reg = new FilterRegistrationBean();
        reg.setFilter(f);
        reg.addUrlPatterns("/v1/*");
        reg.setOrder(2);
        reg.setAsyncSupported(true);
        return reg;

    }


}
