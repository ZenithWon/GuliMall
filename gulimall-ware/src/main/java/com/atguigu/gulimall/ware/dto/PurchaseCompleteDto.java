package com.atguigu.gulimall.ware.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseCompleteDto {
    private Long id;
    private List<CompleteDetail> items;
}
