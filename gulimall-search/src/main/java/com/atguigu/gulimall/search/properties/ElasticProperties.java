package com.atguigu.gulimall.search.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "es")
@Data
public class ElasticProperties {
    private String host;
    private Integer port;
}
