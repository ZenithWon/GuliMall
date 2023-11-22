package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.constant.IndexConstant;
import com.atguigu.gulimall.search.service.ElasticSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ElasticSaveServiceImpl implements ElasticSaveService {
    @Autowired
    RestHighLevelClient client;

    @Override
    public void productPublish(List<SkuEsModel> esModels) {
        esModels.forEach((item)->{
            IndexRequest request=new IndexRequest(IndexConstant.PRODUCT_INDEX).id(item.getSkuId().toString());

            request.source(JSON.toJSONString(item) ,XContentType.JSON);
            try {
                IndexResponse index = client.index(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new GulimallException(ErrorEnum.ELASTICSEARCH_SAVE_ERROR);
            }
        });
    }
}
