package com.atguigu.gulimall.coupon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.common.to.SpuBoundsTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.coupon.dao.SpuBoundsDao;
import com.atguigu.gulimall.coupon.entity.SpuBoundsEntity;
import com.atguigu.gulimall.coupon.service.SpuBoundsService;


@Service("spuBoundsService")
@Slf4j
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsDao, SpuBoundsEntity> implements SpuBoundsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuBoundsEntity> page = this.page(
                new Query<SpuBoundsEntity>().getPage(params),
                new QueryWrapper<SpuBoundsEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(SpuBoundsTo to) {
        SpuBoundsEntity entity= BeanUtil.copyProperties(to,SpuBoundsEntity.class);
        log.debug(entity.toString());
    }

}
