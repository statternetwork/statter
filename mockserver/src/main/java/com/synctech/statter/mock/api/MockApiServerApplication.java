package com.statter.statter.mock.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication(scanBasePackages = {//
        "com.statter.statter.constant", //
        "com.statter.statter.redis",//
        "com.statter.statter.mock.api"//
})
public class MockApiServerApplication {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(MockApiServerApplication.class, args);
    }

}
