package com.atguigu.gulimall.ware.feign;


import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-product")
public interface ProductFeignClient {
    @GetMapping("product/skuinfo/skuName/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);
}
