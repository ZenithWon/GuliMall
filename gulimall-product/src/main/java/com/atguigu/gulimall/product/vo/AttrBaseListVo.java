package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.AttrEntity;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AttrBaseListVo extends AttrEntity {
    private String groupName;
    private String catelogName;
}
