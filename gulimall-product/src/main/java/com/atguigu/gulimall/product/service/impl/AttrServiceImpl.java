package com.atguigu.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.atguigu.common.constants.ProductAttrTypeConstant;
import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.dto.AttrDto;
import com.atguigu.gulimall.product.vo.AttrBaseListVo;
import com.atguigu.gulimall.product.vo.AttrInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
@Slf4j
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrDto dto) {
        AttrEntity attrEntity= BeanUtil.copyProperties(dto,AttrEntity.class);
        if(baseMapper.insert(attrEntity)<=0){
            throw new GulimallException(ErrorEnum.DATABASE_INSERT_ERROR);
        }

        if(dto.getAttrType()== ProductAttrTypeConstant.SALE_ATTR_CODE||dto.getAttrGroupId()==null){
            return;
        }

        AttrAttrgroupRelationEntity relationEntity=new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationEntity.setAttrGroupId(dto.getAttrGroupId());
        if(attrAttrgroupRelationDao.insert(relationEntity)<=0){
            throw new GulimallException(ErrorEnum.DATABASE_INSERT_ERROR);
        }
    }

    @Override
    public PageUtils listBaseByCategoryId(Map<String, Object> params ,String attrType, Long catelogId) {
        String key=(String) params.get("key");
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();
        if(catelogId!=0){
            wrapper.eq(AttrEntity::getCatelogId,catelogId);
        }

        if(attrType.equals(ProductAttrTypeConstant.BASE_ATTR)){
            wrapper.eq(AttrEntity::getAttrType, ProductAttrTypeConstant.BASE_ATTR_CODE);
        }else if (attrType.equals(ProductAttrTypeConstant.SALE_ATTR)){
            wrapper.eq(AttrEntity::getAttrType, ProductAttrTypeConstant.SALE_ATTR_CODE);
        }else{
            throw new GulimallException(ErrorEnum.ILLEGAL_REQUEST);
        }

        wrapper.and(!StrUtil.isBlank(key) ,(obj)->{
            obj.like(AttrEntity::getAttrName,key).or().eq(AttrEntity::getAttrId,key);
        });

        IPage<AttrEntity> page=this.page(new Query<AttrEntity>().getPage(params), wrapper);

        List<AttrBaseListVo> voList = page.getRecords().stream().map((item) -> {
            AttrBaseListVo vo = BeanUtil.copyProperties(item , AttrBaseListVo.class);
            CategoryEntity categoryEntity = categoryDao.selectById(item.getCatelogId());
            if(categoryEntity!=null){
                vo.setCatelogName(categoryEntity.getName());
            }

            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId , item.getAttrId())
            );

            if(relationEntity!=null){
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if(attrGroupEntity!=null){
                    vo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            return vo;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(voList);

        return pageUtils;
    }

    @Override
    public AttrEntity getAttrInfo(Long attrId) {
        AttrEntity attrEntity = baseMapper.selectById(attrId);
        AttrInfoVo vo=BeanUtil.copyProperties(attrEntity,AttrInfoVo.class);

        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq(AttrAttrgroupRelationEntity::getAttrId , attrId)
        );
        if(relationEntity!=null){
            vo.setAttrGroupId(relationEntity.getAttrGroupId());
        }

        List<Long> path=new ArrayList<>();
        CategoryEntity categoryEntity = categoryDao.selectById(vo.getCatelogId());
        path.add(categoryEntity.getCatId());
        while(categoryEntity.getParentCid()!=0){
            categoryEntity=categoryDao.selectById(categoryEntity.getParentCid());
            path.add(0,categoryEntity.getCatId());
        }
        vo.setCatelogPath(path);

        return vo;
    }

    @Override
    @Transactional
    public void updateAttr(AttrDto dto) {
        int update;
        update=baseMapper.updateById(dto);
        if(update<=0){
            throw new GulimallException(ErrorEnum.DATABASE_UPDATE_ERROR);
        }

        if(dto.getAttrType()== ProductAttrTypeConstant.SALE_ATTR_CODE||dto.getAttrGroupId()==null){
            return;
        }

        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>()
                        .eq(AttrAttrgroupRelationEntity::getAttrId , dto.getAttrId())
        );

        if(relationEntity!=null){
            relationEntity.setAttrGroupId(dto.getAttrGroupId());
            update=attrAttrgroupRelationDao.updateById(relationEntity);
        }else{
            relationEntity=new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(dto.getAttrId());
            relationEntity.setAttrGroupId(dto.getAttrGroupId());
            update=attrAttrgroupRelationDao.insert(relationEntity);
        }

        if(update<=0){
            throw new GulimallException(ErrorEnum.DATABASE_UPDATE_ERROR);
        }

    }

}
