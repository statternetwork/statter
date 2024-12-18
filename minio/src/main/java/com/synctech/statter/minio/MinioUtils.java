// package com.statter.statter.minio;

// import io.minio.MinioClient;

// public final class MinioUtils {

//     private static int DEFAULT_EXPIRE_SECOND = 0;

//     private static MinioClient MINIO_CLIENT;

//     /**
//      * init the connection
//      *
//      * @param endpoint     protocol://host:port，default port is 9000
//      * @param accessKey    username
//      * @param secretKey    password
//      * @param expireSecond expire second of presigned url，default is 7 days
//      * @throws Exception
//      */
//     public static void init(String endpoint, String accessKey, String secretKey, int expireSecond) throws Exception {
//         DEFAULT_EXPIRE_SECOND = expireSecond;
//         MINIO_CLIENT = new MinioClient(endpoint, accessKey, secretKey);
//     }

//     public static MinioClient getMinioClient() {
//         return MINIO_CLIENT;
//     }

// }
