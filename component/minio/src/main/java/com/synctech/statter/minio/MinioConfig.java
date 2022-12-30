package com.synctech.statter.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class MinioConfig {


    @Value("${minio.url}")
    private String miniourl;
    @Value("${minio.accessKey}")
    private String minioak;
    @Value("${minio.secretKey}")
    private String minisk;

    @Bean
    public MinioClient minioInit() throws Exception {
       return new MinioClient(miniourl,minioak,minisk);
    }

}
