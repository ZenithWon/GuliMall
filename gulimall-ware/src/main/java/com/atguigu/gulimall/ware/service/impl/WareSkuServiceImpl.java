package com.atguigu.gulimall.ware.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gulimall.ware.feign.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();

        String skuId=(String) params.get("skuId");
        if(!StrUtil.isBlank(skuId)){
            wrapper.eq(WareSkuEntity::getSkuId,Long.parseLong(skuId));
        }

        String wareId=(String) params.get("wareId");
        if(!StrUtil.isBlank(wareId)){
            wrapper.eq(WareSkuEntity::getWareId,Long.parseLong(wareId));
        }

        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void addStockForSkuToWare(Long skuId , Long wareId , Integer stock) {
        WareSkuEntity entity = baseMapper.selectOne(
                new LambdaQueryWrapper<WareSkuEntity>()
                        .eq(WareSkuEntity::getSkuId , skuId)
                        .eq(WareSkuEntity::getWareId,wareId)
        );

        String skuName =(String) productFeignClient.getSkuInfo(skuId).getData();

        if(entity==null){
            entity=new WareSkuEntity();
            entity.setSkuId(skuId);
            entity.setStock(stock);
            entity.setWareId(wareId);
            entity.setSkuName(skuName);
            save(entity);
        }else{
            update(
                    new LambdaUpdateWrapper<WareSkuEntity>()
                            .eq(WareSkuEntity::getSkuId , skuId)
                            .eq(WareSkuEntity::getWareId,wareId)
                            .set(WareSkuEntity::getStock,entity.getStock()+stock)
            );
        }
    }

    @Override
    public Long getStockBySkuId(Long skuId) {
        Long saleStock = baseMapper.getSaleStockBySkuId(skuId);

        return saleStock;
    }

}
