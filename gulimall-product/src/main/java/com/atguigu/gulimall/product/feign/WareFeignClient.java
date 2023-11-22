package com.atguigu.gulimall.product.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-ware")
public interface WareFeignClient {
    @GetMapping("/ware/waresku/stock/{skuId}")
    public R getStock(@PathVariable Long skuId);
}
