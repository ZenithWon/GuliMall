package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constants.WarePurchaseStatusConstant;
import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.atguigu.gulimall.ware.dto.CompleteDetail;
import com.atguigu.gulimall.ware.dto.MergeDto;
import com.atguigu.gulimall.ware.dto.PurchaseCompleteDto;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils listUnReceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new LambdaQueryWrapper<PurchaseEntity>()
                        .eq(PurchaseEntity::getStatus , WarePurchaseStatusConstant.PURCHASE_NEW)
                        .or().eq(PurchaseEntity::getStatus , WarePurchaseStatusConstant.PURCHASE_ALLOCATED)
        );
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchaseDetail(MergeDto dto) {
        Long purchaseId = dto.getPurchaseId();
        if(purchaseId==null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setPriority(1);
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WarePurchaseStatusConstant.PURCHASE_NEW);
            save(purchaseEntity);
            purchaseId=purchaseEntity.getId();
        }

        Long finalPurchaseId = purchaseId;
        dto.getItems().forEach((item)->{
            PurchaseDetailEntity entity = purchaseDetailService.getById(item);
            Integer status = entity.getStatus();
            if(!status.equals(WarePurchaseStatusConstant.PURCHASE_DETAIL_NEW)){
                throw new GulimallException(ErrorEnum.PURCHASE_MERGE_ERROR);
            }
            entity.setPurchaseId(finalPurchaseId);
            entity.setStatus(WarePurchaseStatusConstant.PURCHASE_DETAIL_ALLOCATED);
            purchaseDetailService.updateById(entity);
        });

        update(
                new LambdaUpdateWrapper<PurchaseEntity>()
                    .eq(PurchaseEntity::getId,purchaseId)
                    .set(PurchaseEntity::getUpdateTime,new Date())
        );
    }

    @Override
    @Transactional
    public void receivePurchase(Long[] purchaseIds) {
        List<Integer> allowUpdatePurchaseStatus=new ArrayList<>();
        allowUpdatePurchaseStatus.add(WarePurchaseStatusConstant.PURCHASE_NEW);
        allowUpdatePurchaseStatus.add(WarePurchaseStatusConstant.PURCHASE_ALLOCATED);

        List<Integer> allowUpdatePurchaseDetailStatus=new ArrayList<>();
        allowUpdatePurchaseDetailStatus.add(WarePurchaseStatusConstant.PURCHASE_DETAIL_ALLOCATED);
        allowUpdatePurchaseDetailStatus.add(WarePurchaseStatusConstant.PURCHASE_DETAIL_NEW);

        for(Long purchaseId:purchaseIds){
            update(
                    new LambdaUpdateWrapper<PurchaseEntity>()
                            .eq(PurchaseEntity::getId,purchaseId)
                            .in(PurchaseEntity::getStatus,allowUpdatePurchaseStatus)
                            .set(PurchaseEntity::getStatus,WarePurchaseStatusConstant.PURCHASE_ACCEPTED)
            );

            purchaseDetailService.update(
                    new LambdaUpdateWrapper<PurchaseDetailEntity>()
                            .eq(PurchaseDetailEntity::getPurchaseId,purchaseId)
                            .in(PurchaseDetailEntity::getStatus,allowUpdatePurchaseDetailStatus)
                            .set(PurchaseDetailEntity::getStatus,WarePurchaseStatusConstant.PURCHASE_DETAIL_BUYING)
            );
        }
    }

    @Override
    @Transactional
    public void completePurchase(PurchaseCompleteDto dto) {
        Long purchaseId = dto.getId();
        int purchaseStatus=WarePurchaseStatusConstant.PURCHASE_COMPLETED;

        for(CompleteDetail item:dto.getItems()){
            Integer status = item.getStatus();

            PurchaseDetailEntity entity=purchaseDetailService.getById(item.getItemId());
            entity.setStatus(status);
            purchaseDetailService.updateById(entity);

            if(status.equals(WarePurchaseStatusConstant.PURCHASE_DETAIL_ERROR)){
                purchaseStatus=WarePurchaseStatusConstant.PURCHASE_ERROR;
                continue;
            }

            wareSkuService.addStockForSkuToWare(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());

        }

        update(
                new LambdaUpdateWrapper<PurchaseEntity>()
                        .eq(PurchaseEntity::getId,purchaseId)
                        .set(PurchaseEntity::getStatus,purchaseStatus)
        );

    }

}
