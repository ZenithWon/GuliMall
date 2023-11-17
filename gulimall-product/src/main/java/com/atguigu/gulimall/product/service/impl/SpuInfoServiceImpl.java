package com.atguigu.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.atguigu.common.to.MemberPriceTo;
import com.atguigu.common.to.SkuFullReductionTo;
import com.atguigu.common.to.SkuLadderTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.gulimall.product.dto.ProductSaveDto;
import com.atguigu.gulimall.product.dto.Sku;
import com.atguigu.gulimall.product.dto.SkuImage;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignClient;
import com.atguigu.gulimall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignClient couponFeignClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        Set<String> query = params.keySet();

        if(query.contains("status")){
            wrapper.eq(SpuInfoEntity::getPublishStatus,Integer.parseInt((String) params.get("status")));
        }
        if(query.contains("brandId")){
            wrapper.eq(SpuInfoEntity::getBrandId,Long.parseLong((String) params.get("brandId")));
        }
        if(query.contains("catelogId")){
            wrapper.eq(SpuInfoEntity::getCatalogId,Long.parseLong((String) params.get("catelogId")));
        }

        String key=(String) params.get("key");
        wrapper.and(!StrUtil.isBlank(key),(obj)->{
            obj.eq(SpuInfoEntity::getId,key)
                    .or().like(SpuInfoEntity::getSpuName,key)
                    .or().like(SpuInfoEntity::getSpuDescription,key);
        });

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    @Transactional
    //FIXME: 失败后远程调用事务回滚
    public void saveProduct(ProductSaveDto dto) {
        SpuInfoEntity spuInfoEntity= BeanUtil.copyProperties(dto,SpuInfoEntity.class);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        save(spuInfoEntity);

        Long spuId=spuInfoEntity.getId();

        SpuInfoDescEntity spuInfoDescEntity=new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(StrUtil.join(",",dto.getDecript()));
        spuInfoDescService.save(spuInfoDescEntity);

        dto.getImages().forEach((item)->{
            SpuImagesEntity entity = new SpuImagesEntity();
            entity.setSpuId(spuId);
            entity.setImgUrl(item);
            spuImagesService.save(entity);
        });

        dto.getBaseAttrs().forEach((item)->{
            ProductAttrValueEntity entity=new ProductAttrValueEntity();
            entity.setAttrId(item.getAttrId());
            entity.setAttrName(attrService.getById(item.getAttrId()).getAttrName());
            entity.setSpuId(spuId);
            entity.setQuickShow(item.getShowDesc());
            entity.setAttrValue(item.getAttrValues());
            productAttrValueService.save(entity);
        });

        SpuBoundsTo spuBoundsTo = BeanUtil.copyProperties(dto.getBounds() , SpuBoundsTo.class);
        spuBoundsTo.setSpuId(spuId);
        if(couponFeignClient.saveSpuBounds(spuBoundsTo).getCode()!=0){
            throw new GulimallException(ErrorEnum.DATABASE_INSERT_ERROR);
        }

        dto.getSkus().forEach(item->saveSku(item,spuInfoEntity));
    }


    public void saveSku(Sku sku,SpuInfoEntity spu){
        Long spuId  =spu.getId();

        SkuInfoEntity skuInfoEntity=BeanUtil.copyProperties(sku,SkuInfoEntity.class);
        skuInfoEntity.setSpuId(spuId);
        skuInfoEntity.setBrandId(spu.getBrandId());
        skuInfoEntity.setCatalogId(spu.getCatalogId());
        skuInfoEntity.setSaleCount(0L);

        SkuImage skuImage = sku.getImages().stream()
                .filter((item) -> item.getDefaultImg() == 1)
                .collect(Collectors.toList()).get(0);
        skuInfoEntity.setSkuDefaultImg(skuImage.getImgUrl());

        skuInfoService.save(skuInfoEntity);
        Long skuId = skuInfoEntity.getSkuId();

        sku.getImages().forEach((item)->{
                    if(!StrUtil.isBlank(item.getImgUrl())){
                        SkuImagesEntity entity = BeanUtil.copyProperties(item , SkuImagesEntity.class);
                        entity.setSkuId(skuId);
                        skuImagesService.save(entity);
                    }
                });

        sku.getAttr().forEach((item)->{
            SkuSaleAttrValueEntity entity=BeanUtil.copyProperties(item,SkuSaleAttrValueEntity.class);
            entity.setSkuId(skuId);
            skuSaleAttrValueService.save(entity);
        });

        if(sku.getFullPrice().compareTo(BigDecimal.ZERO)>0){
            SkuFullReductionTo skuFullReductionTo=new SkuFullReductionTo();
            skuFullReductionTo.setSkuId(skuId);
            skuFullReductionTo.setFullPrice(sku.getFullPrice());
            skuFullReductionTo.setReducePrice(sku.getReducePrice());
            skuFullReductionTo.setAddOther(sku.getPriceStatus());
            if(couponFeignClient.saveSkuFullReduction(skuFullReductionTo).getCode()!=0){
                throw new GulimallException(ErrorEnum.DATABASE_INSERT_ERROR);
            }
        }

        if(sku.getFullCount()>0){
            SkuLadderTo skuLadderTo=new SkuLadderTo();
            skuLadderTo.setSkuId(skuId);
            skuLadderTo.setFullCount(sku.getFullCount());
            skuLadderTo.setDiscount(sku.getDiscount());
            skuLadderTo.setAddOther(sku.getCountStatus());
            skuLadderTo.setPrice(sku.getPrice().multiply(sku.getDiscount()));
            if(couponFeignClient.saveSkuLadder(skuLadderTo).getCode()!=0){
                throw new GulimallException(ErrorEnum.DATABASE_INSERT_ERROR);
            }
        }

        sku.getMemberPrice().forEach((item)->{
            MemberPriceTo memberPriceTo=new MemberPriceTo();
            memberPriceTo.setSkuId(skuId);
            memberPriceTo.setMemberLevelId(item.getId());
            memberPriceTo.setMemberPrice(item.getPrice());
            memberPriceTo.setMemberLevelName(item.getName());
            memberPriceTo.setAddOther(1);
            if(couponFeignClient.saveMemberPrice(memberPriceTo).getCode()!=0){
                throw new GulimallException(ErrorEnum.DATABASE_INSERT_ERROR);
            }
        });

    }

}
