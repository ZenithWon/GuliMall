package com.atguigu.gulimall.tools.config;

import com.atguigu.gulimall.tools.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient(){
        MinioClient minioClient= null;
        try {
            minioClient = new MinioClient(
                    minioProperties.getEndpoint(), minioProperties.getAccesskey(), minioProperties.getSecretKey());
        } catch (Exception e) {
            throw new RuntimeException("初始化minio失败");
        }
        return minioClient;
    }
}
