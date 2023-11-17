package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.dto.MergeDto;
import com.atguigu.gulimall.ware.dto.PurchaseCompleteDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zenith
 * @email sunlightcs@gmail.com
 * @date 2023-11-14 14:20:07
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils listUnReceivePurchase(Map<String, Object> params);

    void mergePurchaseDetail(MergeDto dto);

    void receivePurchase(Long[] purchaseIds);

    void completePurchase(PurchaseCompleteDto dto);
}

