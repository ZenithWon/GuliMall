package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.dto.SpuAttrDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author zenith
 * @email sunlightcs@gmail.com
 * @date 2023-11-14 13:17:13
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ProductAttrValueEntity> listAttrForSpu(Long spuId);

    void updateAttrForSpu(Long spuId , List<SpuAttrDto> dtos);
}

