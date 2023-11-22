package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.util.List;

public interface ElasticSaveService {
    void productPublish(List<SkuEsModel> esModels);
}
