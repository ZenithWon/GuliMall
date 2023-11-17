package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.dto.AttrRelationDto;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 属性分组
 *
 * @author zenith
 * @email sunlightcs@gmail.com
 * @date 2023-11-14 13:35:33
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable Long categoryId){

        PageUtils page = attrGroupService.queryPage(params , categoryId);
        return R.ok().put("page", page);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R listAttrRelationByAttrGid(@PathVariable Long attrgroupId){
        List<AttrEntity> attrList=attrGroupService.listAttrRelationByAttrGid(attrgroupId);

        return R.success(attrList);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R listAttrNoRelationByAttrGid(@RequestParam Map<String, Object> params,@PathVariable Long attrgroupId){
        PageUtils page=attrGroupService.listAttrNoRelationByAttrGid(params,attrgroupId);

        return R.pageSuccess(page);
    }

    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelationBatch(@RequestBody AttrRelationDto[] deleteDtos){
        attrGroupService.deleteAttrRelationBatch(Arrays.asList(deleteDtos));
        return R.ok();
    }

    @PostMapping("/attr/relation")
    public R saveAttrRelation(@RequestBody AttrRelationDto[] relationDtos){
        attrGroupService.saveAttrRelation(relationDtos);
        return R.ok();
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrInfo(@PathVariable Long catelogId){
        List<AttrGroupWithAttrVo> voList=attrGroupService.getAttrGroupWithAttrInfo(catelogId);

        return R.success(voList);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
