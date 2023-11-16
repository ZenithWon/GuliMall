package com.atguigu.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.common.constants.ProductAttrTypeConstant;
import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.dto.AttrRelationDto;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public AttrGroupEntity getById(Serializable id) {
        AttrGroupEntity entity = super.getById(id);
        resolveEntity(entity);
        return entity;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params , Long categoryId) {
        IPage<AttrGroupEntity> page=null;
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        String key=(String) params.get("key");

        wrapper.and(!StringUtils.isBlank(key),(obj)->{
            obj.eq( AttrGroupEntity::getAttrGroupId,key)
                    .or()
                    .like(AttrGroupEntity::getAttrGroupName,key);
        });

        if(categoryId==0){
            page = this.page(new Query<AttrGroupEntity>().getPage(params) , wrapper);
        }else{

            page = this.page(new Query<AttrGroupEntity>().getPage(params) ,
                    wrapper.eq(AttrGroupEntity::getCatelogId,categoryId)
            );
        }

        page.getRecords().forEach(this::resolveEntity);
        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> listAttrRelationByAttrGid(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(
                new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq(AttrAttrgroupRelationEntity::getAttrGroupId , attrgroupId)
        );

        List<AttrEntity> attrEntities = relationEntities.stream()
                .map((item) -> attrDao.selectById(item.getAttrId()))
                .collect(Collectors.toList());

        return attrEntities;
    }

    @Override
    @Transactional
    public void deleteAttrRelationBatch(List<AttrRelationDto> deleteDtos) {
        for(AttrRelationDto dto:deleteDtos){
            attrAttrgroupRelationDao.delete(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrGroupId,dto.getAttrGroupId())
                            .eq(AttrAttrgroupRelationEntity::getAttrId,dto.getAttrId())
            );
        }
    }

    @Override
    public PageUtils listAttrNoRelationByAttrGid(Map<String, Object> params,Long attrgroupId) {
        Long catelogId = baseMapper.selectById(attrgroupId).getCatelogId();

        List<Long> existAttrIds = attrAttrgroupRelationDao.selectList(null)
                .stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrEntity::getCatelogId,catelogId).eq(AttrEntity::getAttrType, ProductAttrTypeConstant.BASE_ATTR_CODE);
        wrapper.notIn(!CollectionUtils.isEmpty(existAttrIds) ,AttrEntity::getAttrId,existAttrIds);

        IPage<AttrEntity> page = attrService.page(
                new Query<AttrEntity>().getPage(params) ,
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttrRelation(AttrRelationDto[] relationDtos) {
        for(AttrRelationDto item:relationDtos){
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId , item.getAttrId())
            );

            if(relationEntity!=null){
                throw new GulimallException(ErrorEnum.DATABASE_DUPLICATE_ERROR);
            }

           relationEntity = BeanUtil.copyProperties(item,AttrAttrgroupRelationEntity.class);

            int insert=attrAttrgroupRelationDao.insert(relationEntity);
            if(insert<=0){
                throw new GulimallException(ErrorEnum.DATABASE_INSERT_ERROR);
            }
        }
    }

    private void resolveEntity(AttrGroupEntity item){
        CategoryEntity categoryEntity = categoryDao.selectById(item.getCatelogId());
        item.setCategoryName(categoryEntity.getName());

        List<Long> path=new ArrayList<>();
        path.add(categoryEntity.getCatId());
        while(categoryEntity.getParentCid()!=0){
            categoryEntity=categoryDao.selectById(categoryEntity.getParentCid());
            path.add(0,categoryEntity.getCatId());
        }
        item.setCatelogPath(path);
    }

}
