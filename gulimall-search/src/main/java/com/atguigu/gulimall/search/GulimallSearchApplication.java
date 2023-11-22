package com.atguigu.gulimall.search;

import com.atguigu.gulimall.search.properties.ElasticProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableConfigurationProperties({ElasticProperties.class})
public class GulimallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSearchApplication.class , args);
    }

}
