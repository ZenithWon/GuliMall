package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.AttrEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class AttrInfoVo extends AttrEntity {

    private Long attrGroupId;
    private List<Long> catelogPath;

}
