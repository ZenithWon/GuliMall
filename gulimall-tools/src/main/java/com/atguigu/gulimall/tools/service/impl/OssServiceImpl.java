package com.atguigu.gulimall.tools.service.impl;

import com.atguigu.gulimall.tools.properties.MinioProperties;
import com.atguigu.gulimall.tools.service.OssService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

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
            objectName=DigestUtils.md5DigestAsHex(file.getInputStream())+".jpg";
            log.debug("Uploading...");
            Long begin=System.currentTimeMillis();
            minioClient.putObject(bucketName,objectName,file.getInputStream(), new PutObjectOptions(objectSize,partSize));
            Long end=System.currentTimeMillis();
            log.debug("Upload completed, consume time: {}ms",end-begin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return minioProperties.getEndpoint()+"/"+bucketName+"/"+objectName;
    }
}
