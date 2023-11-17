package com.atguigu.gulimall.ware.service.impl;

import cn.hutool.core.util.StrUtil;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDetailDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();

        String wareId=(String) params.get("wareId");
        if(!StrUtil.isBlank(wareId)){
            wrapper.eq(PurchaseDetailEntity::getWareId,Long.parseLong(wareId));
        }

        String status=(String) params.get("status");
        if(!StrUtil.isBlank(status)){
            wrapper.eq(PurchaseDetailEntity::getStatus,Long.parseLong(status));
        }

        String key=(String) params.get("key");
        if(!StrUtil.isBlank(key)){
            wrapper.and((w)->{
                w.eq(PurchaseDetailEntity::getSkuId,key).or().eq(PurchaseDetailEntity::getPurchaseId,key);
            });
        }

        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

}
