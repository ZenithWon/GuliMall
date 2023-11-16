package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.dto.AttrDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author zenith
 * @email sunlightcs@gmail.com
 * @date 2023-11-14 13:17:13
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrDto attr);

    PageUtils listBaseByCategoryId(Map<String, Object> params , String attrType,Long catelogId);

    AttrEntity getAttrInfo(Long attrId);

    void updateAttr(AttrDto dto);
}

