package com.atguigu.gulimall.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Sku {
    private List<SkuAttr> attr;
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;
    private List<SkuImage> images;
    private List<String> descar;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
