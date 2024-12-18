package com.statter.statter.administrator.api;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@MapperScan("com.statter.statter.base.mapper")
@SpringBootApplication(scanBasePackages = {//
        "com.statter.statter.base.entity", //
        "com.statter.statter.constant", //
        "com.statter.statter.redis",//
        "com.statter.statter.common.service", //
        "com.statter.statter.administrator.api"//
})
public class AdministratorApiServerApplication {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(AdministratorApiServerApplication.class, args);
    }

}
