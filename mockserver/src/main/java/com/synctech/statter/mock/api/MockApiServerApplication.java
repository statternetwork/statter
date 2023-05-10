package com.synctech.statter.mock.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication(scanBasePackages = {//
        "com.synctech.statter.constant", //
        "com.synctech.statter.redis",//
        "com.synctech.statter.mock.api"//
})
public class MockApiServerApplication {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(MockApiServerApplication.class, args);
    }

}
