package com.atguigu.gulimall.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.atguigu.gulimall.product.constant.RedisKeys;
import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.utils.BasicDistributedLock;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    BasicDistributedLock distributedLock;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        List<CategoryEntity> firstCategory = categoryEntities.stream()
                .filter((item) -> item.getParentCid().equals(0L)).map((item)->{
                    item.setChildren(getChildren(item,categoryEntities));
                    return item;
                }).sorted((item1 , item2) -> {
                    return (item1.getSort()==null?0:item1.getSort()) - (item2.getSort()==null?0:item2.getSort());
                }).collect(Collectors.toList());

        return firstCategory;
    }

    @Override
    public void removeCategoryByIds(List<Long> categoryIds) {
        //FIXME：检查是否可以删除，查询是否关联
        baseMapper.deleteBatchIds(categoryIds);
    }

    @Override
    public void updateSort(CategoryEntity[] category) {
        updateBatchById(Arrays.asList(category));
    }

    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        baseMapper.updateById(category);
        int update = categoryBrandRelationDao.update(null ,
                new LambdaUpdateWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getCatelogId , category.getCatId())
                        .set(CategoryBrandRelationEntity::getCatelogName , category.getName())

        );
        if(update<=0){
            throw new GulimallException(ErrorEnum.DATABASE_UPDATE_ERROR);
        }
    }

    @Override
    public List<CategoryEntity> getByParentId(Long parentId) {
        return baseMapper.selectList(
                new LambdaUpdateWrapper<CategoryEntity>()
                    .eq(CategoryEntity::getParentCid,parentId)
        );
    }

    @Override
    public JSONObject getCatalogJson() {
        String json=redisTemplate.opsForValue().get(RedisKeys.CATEGORY_INDEX_JSON);
        if(StrUtil.isNotBlank(json)){
            return JSONObject.parseObject(json);
        }

        RLock lock = redissonClient.getLock(RedisKeys.CATEGORY_INDEX_JSON_LOCK);
        lock.lock();
        try{
            json = getCategoryJsonFromDb();
            return JSONObject.parseObject(json);
        }catch (Exception e){
            throw new GulimallException(ErrorEnum.DATABASE_ERROR);
        }finally {
            lock.unlock();
        }

    }

    private String getCategoryJsonFromDb(){
        String json=redisTemplate.opsForValue().get(RedisKeys.CATEGORY_INDEX_JSON);
        if(StrUtil.isNotBlank(json)){
            return json;
        }

        List<CategoryEntity> levelOne = getByParentId(0L);

        Map<String, Object> map = levelOne.stream().collect(Collectors.toMap(k -> k.getCatId().toString() , v -> {
            List<CategoryEntity> levelTwo = getByParentId(v.getCatId());
            List<Catelog2Vo> catelog2VoList = levelTwo.stream().map((item) -> {
                List<CategoryEntity> levelThree = getByParentId(item.getCatId());

                List<Object> catalog3VoList = levelThree.stream().map((entity) -> {
                    return new Catelog2Vo.Catalog3Vo(entity.getParentCid().toString() , entity.getCatId().toString() , entity.getName());
                }).collect(Collectors.toList());

                return new Catelog2Vo(v.getCatId().toString() ,
                        catalog3VoList,
                        item.getCatId().toString() ,
                        item.getName());
            }).collect(Collectors.toList());
            return catelog2VoList;
        }));
        json=JSON.toJSONString(map);
        redisTemplate.opsForValue().set(RedisKeys.CATEGORY_INDEX_JSON,json);

        return json;
    }

    private List<CategoryEntity> getChildren(CategoryEntity current,List<CategoryEntity> all){
        List<CategoryEntity> collect = all.stream().filter((item) -> item.getParentCid().equals(current.getCatId())).map((item) -> {
            item.setChildren(getChildren(item , all));
            return item;
        }).sorted((item1 , item2) -> {
            return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collect)){
            return null;
        }
        return collect;
    }

}
