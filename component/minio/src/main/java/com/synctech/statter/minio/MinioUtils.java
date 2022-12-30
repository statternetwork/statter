package com.synctech.statter.minio;

import io.minio.MinioClient;

public final class MinioUtils {

    private static int DEFAULT_EXPIRE_SECOND = 0;

    private static MinioClient MINIO_CLIENT;

    /**
     * init the connection
     *
     * @param endpoint     protocol://host:port，default port is 9000
     * @param accessKey    username
     * @param secretKey    password
     * @param expireSecond expire second of presigned url，default is 7 days
     * @throws Exception
     */
    public static void init(String endpoint, String accessKey, String secretKey, int expireSecond) throws Exception {
        DEFAULT_EXPIRE_SECOND = expireSecond;
        // greater than 8.0
//        MinioClient.Builder builder = MinioClient.builder();
//        builder = builder.endpoint(endpoint).credentials(accessKey, secretKey);
//        MINIO_CLIENT = builder.build();
        // below ver 8.0
        MINIO_CLIENT = new MinioClient(endpoint, accessKey, secretKey);
    }

    public static MinioClient getMinioClient() {
        return MINIO_CLIENT;
    }

}
