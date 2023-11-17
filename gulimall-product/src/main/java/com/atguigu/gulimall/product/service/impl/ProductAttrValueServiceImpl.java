package com.atguigu.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.gulimall.product.dto.SpuAttrDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.ProductAttrValueDao;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> listAttrForSpu(Long spuId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<ProductAttrValueEntity>()
                        .eq(ProductAttrValueEntity::getSpuId,spuId)
        );
    }

    @Override
    @Transactional
    public void updateAttrForSpu(Long spuId , List<SpuAttrDto> dtos) {
        baseMapper.delete(
                new LambdaQueryWrapper<ProductAttrValueEntity>()
                        .eq(ProductAttrValueEntity::getSpuId,spuId)
        );

        dtos.forEach((item)->{
            ProductAttrValueEntity entity= BeanUtil.copyProperties(item,ProductAttrValueEntity.class);
            entity.setSpuId(spuId);
            save(entity);
        });
    }

}
