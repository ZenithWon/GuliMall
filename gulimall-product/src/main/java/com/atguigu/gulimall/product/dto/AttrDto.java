package com.atguigu.gulimall.product.dto;

import com.atguigu.gulimall.product.entity.AttrEntity;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AttrDto extends AttrEntity {


    private Long attrGroupId;
}
