package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandDao brandDao;
    @Autowired
    private CategoryDao categoryDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> listCategoryByBrandId(Long brandId) {

        return baseMapper.selectList(
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getBrandId,brandId)
        );
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        CategoryBrandRelationEntity dbEntity = baseMapper.selectOne(
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getBrandId , brandId)
                        .eq(CategoryBrandRelationEntity::getCatelogId , catelogId)
        );

        if(dbEntity!=null){
            throw new GulimallException(ErrorEnum.DATABASE_DUPLICATE_ERROR);
        }
        categoryBrandRelation.setBrandName(brandDao.selectById(brandId).getName());
        categoryBrandRelation.setCatelogName(categoryDao.selectById(catelogId).getName());

        if(baseMapper.insert(categoryBrandRelation)<=0){
            throw new GulimallException(ErrorEnum.DATABASE_INSERT_ERROR);
        }
    }

    @Override
    public List<BrandEntity> listBrandsByCategoryId(Long catId) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = baseMapper.selectList(
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getCatelogId , catId)
        );

        List<BrandEntity> brandEntities = categoryBrandRelationEntities
                .stream()
                .map((item) -> brandDao.selectById(item.getBrandId()))
                .collect(Collectors.toList());

        return brandEntities;
    }

}
