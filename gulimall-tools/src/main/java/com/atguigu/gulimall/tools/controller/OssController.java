package com.atguigu.gulimall.tools.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.tools.service.OssService;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/tool/oss")
public class OssController {
    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    public R uploadPic(MultipartFile file){
        return R.success(ossService.upload(file));
    }

}
