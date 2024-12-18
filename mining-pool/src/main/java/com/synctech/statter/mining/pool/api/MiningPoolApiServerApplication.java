package com.statter.statter.mining.pool.api;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@MapperScan("com.statter.statter.base.mapper")
@SpringBootApplication(scanBasePackages = {//
        "com.statter.statter.base.entity", //
        "com.statter.statter.redis",//
        "com.statter.statter.constant.swagger",//
        "com.statter.statter.common.service", //
        "com.statter.statter.mining.pool.api",//
})
public class MiningPoolApiServerApplication {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(MiningPoolApiServerApplication.class, args);
    }

}
