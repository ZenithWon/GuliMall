package com.atguigu.gulimall.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttrRelationDto {
    private Long attrId;
    private Long attrGroupId;
}
