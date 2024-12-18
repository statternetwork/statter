package com.statter.statter.manager.api;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@MapperScan("com.statter.statter.base.mapper")
@SpringBootApplication(scanBasePackages = {//
        "com.statter.statter.base.entity", //
        "com.statter.statter.constant", //
        "com.statter.statter.redis",//
        "com.statter.statter.common.pool", //
        "com.statter.statter.common.service", //
        "com.statter.statter.manager.api"//
})
public class ManagerApiServerApplication {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(ManagerApiServerApplication.class, args);
    }

}
