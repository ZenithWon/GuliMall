package com.atguigu.gulimall.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        Set<String> query = params.keySet();

        if(!"0".equals(params.get("brandId"))){
            wrapper.eq(SkuInfoEntity::getBrandId,Long.parseLong((String) params.get("brandId")));
        }
        if(!"0".equals(params.get("catelogId"))){
            wrapper.eq(SkuInfoEntity::getCatalogId,Long.parseLong((String) params.get("catelogId")));
        }

        BigDecimal min = BigDecimal.valueOf(Long.parseLong((String) params.get("min")));
        if(min.compareTo(BigDecimal.ZERO)>0){
            wrapper.ge(SkuInfoEntity::getPrice, min );
        }

        BigDecimal max = BigDecimal.valueOf(Long.parseLong((String) params.get("max")));
        if(max.compareTo(min)>0){
            wrapper.le(SkuInfoEntity::getPrice, BigDecimal.valueOf(Long.parseLong((String) params.get("max"))));
        }else if(max.compareTo(BigDecimal.ZERO)>0){
            throw new GulimallException(ErrorEnum.VALID_EXCEPTION);
        }

        String key=(String) params.get("key");
        wrapper.and(!StrUtil.isBlank(key),(obj)->{
            obj.eq(SkuInfoEntity::getSkuId,key)
                    .or().like(SkuInfoEntity::getSkuName,key);
        });

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = baseMapper.selectList(
                new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId , spuId)
        );
        return skuInfoEntities;
    }

}
