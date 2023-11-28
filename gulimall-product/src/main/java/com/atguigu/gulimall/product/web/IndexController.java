package com.atguigu.gulimall.product.web;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/" , "/index.tml"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntityList = categoryService.getByParentId(0L);

        model.addAttribute("categories",categoryEntityList);

        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public JSONObject getCatalogJson(){
        return categoryService.getCatalogJson();
    }
}
