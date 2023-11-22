package com.atguigu.gulimall.search.config;

import com.atguigu.gulimall.search.properties.ElasticProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {

    @Autowired
    private ElasticProperties esProperties;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client=new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esProperties.getHost(),esProperties.getPort())
                )
        );
        return client;
    }
}
