package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.dto.AttrRelationDto;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zenith
 * @email sunlightcs@gmail.com
 * @date 2023-11-14 13:17:13
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params , Long categoryId);

    List<AttrEntity> listAttrRelationByAttrGid(Long attrgroupId);

    void deleteAttrRelationBatch(List<AttrRelationDto> deleteDtos);

    PageUtils listAttrNoRelationByAttrGid(Map<String, Object> params,Long attrgroupId);

    void saveAttrRelation(AttrRelationDto[] relationDtos);

    List<AttrGroupWithAttrVo> getAttrGroupWithAttrInfo(Long catelogId);
}

