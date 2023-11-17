package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class AttrGroupWithAttrVo extends AttrGroupEntity {
    private List<AttrInfoVo> attrs;
}
