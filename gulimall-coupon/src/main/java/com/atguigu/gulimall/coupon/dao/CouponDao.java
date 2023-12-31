package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zenith
 * @email sunlightcs@gmail.com
 * @date 2023-11-14 14:07:45
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
