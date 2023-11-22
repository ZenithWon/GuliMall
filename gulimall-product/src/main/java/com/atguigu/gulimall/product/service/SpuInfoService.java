package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.dto.ProductSaveDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author zenith
 * @email sunlightcs@gmail.com
 * @date 2023-11-14 13:17:13
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveProduct(ProductSaveDto dto);

    void publish(Long spuId);
}

