package com.synctech.statter.ledger.api;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@MapperScan("com.synctech.statter.base.mapper")
@SpringBootApplication(scanBasePackages = {//
        "com.synctech.statter.base.entity", //
        "com.synctech.statter.constant", //
        "com.synctech.statter.redis",//
        "com.synctech.statter.common.pool", //
        "com.synctech.statter.common.service", //
        "com.synctech.statter.ledger.api"//
})
public class LedgerApiServerApplication {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(LedgerApiServerApplication.class, args);
    }

}
