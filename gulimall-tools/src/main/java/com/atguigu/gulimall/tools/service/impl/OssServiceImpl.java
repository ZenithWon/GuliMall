package com.atguigu.gulimall.tools.service.impl;

import com.atguigu.common.exception.ErrorEnum;
import com.atguigu.common.exception.GulimallException;
import com.atguigu.gulimall.tools.properties.MinioProperties;
import com.atguigu.gulimall.tools.service.OssService;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class OssServiceImpl implements OssService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    private static long objectSize=-1L;
    private static long partSize=5368709120L;

    @Override
    public String upload(MultipartFile file) {
        String name = file.getName();
        String bucketName=minioProperties.getBucketName();
        String objectName= null;
        try {
            objectName = DigestUtils.md5DigestAsHex(file.getInputStream())+".jpg";
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean exist=true;
        try {
            minioClient.statObject(bucketName,objectName);
        } catch (Exception e) {
            exist=false;
        }

        log.debug("Uploading...");
        Long begin=System.currentTimeMillis();

        if(exist){
            log.debug("File already has been uploaded!");
        }else{
            try {
                minioClient.putObject(bucketName,objectName,file.getInputStream(), new PutObjectOptions(objectSize,partSize));
                Long end=System.currentTimeMillis();
                log.debug("Upload completed, consume time: {}ms",end-begin);
            } catch (Exception e) {
                throw new GulimallException(ErrorEnum.UPLOAD_FIGURE_FAILED);
            }
        }

        return minioProperties.getEndpoint()+"/"+bucketName+"/"+objectName;
    }
}
