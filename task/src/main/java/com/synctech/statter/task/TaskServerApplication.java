package com.synctech.statter.task;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@MapperScan("com.synctech.statter.base.mapper")
@SpringBootApplication(scanBasePackages = {//
        "com.synctech.statter.base.entity", //
//        "com.synctech.statter.constant", //
        "com.synctech.statter.redis",//
        "com.synctech.statter.common.pool", //
        "com.synctech.statter.common.service", //
        "com.synctech.statter.task"//
})
public class TaskServerApplication {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(TaskServerApplication.class, args);
    }

}
