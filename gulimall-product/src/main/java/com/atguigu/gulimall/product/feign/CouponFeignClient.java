package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.MemberPriceTo;
import com.atguigu.common.to.SkuFullReductionTo;
import com.atguigu.common.to.SkuLadderTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignClient {
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo to);

    @PostMapping("/coupon/skuladder/save")
    R saveSkuLadder(@RequestBody SkuLadderTo to);

    @PostMapping("/coupon/skufullreduction/save")
    R saveSkuFullReduction(@RequestBody SkuFullReductionTo to);

    @PostMapping("/coupon/memberprice/save")
    R saveMemberPrice(@RequestBody MemberPriceTo to);
}
