package com.atguigu.gulimall.tools;

import com.atguigu.gulimall.tools.properties.MinioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({MinioProperties.class})
public class  GulimallToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallToolsApplication.class , args);
    }

}
