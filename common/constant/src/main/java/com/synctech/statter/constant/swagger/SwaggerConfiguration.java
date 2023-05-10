package com.synctech.statter.constant.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Access url：http://localhost:58080/statter/manager/api/swagger-ui.html
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Value("${swagger.enable}")
    private Boolean enableSwagger;

    @Bean
    public Docket docket(Environment env) {
        return new Docket(DocumentationType.SWAGGER_2).enable(enableSwagger).select().build();
    }

}
