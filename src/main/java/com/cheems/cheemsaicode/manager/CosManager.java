package com.cheems.cheemsaicode.manager;

import java.io.File;

import org.springframework.stereotype.Component;

import com.cheems.cheemsaicode.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CosManager {

    @Resource
    private COSClient cosClient;
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 上传对象
     * 
     * @param key
     * @param file
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传文件到cos并返回url
     * 
     * @param key
     *            cos对象key
     * @param file
     *            本地文件
     * @return cos对象url
     */
    public String uploadFile(String key, File file) {
        PutObjectResult putObjectResult = putObject(key, file);
        if (putObjectResult != null) {
            String url = String.format("%s%s", cosClientConfig.getHost(), key);
            log.info("上传文件{}到cos成功,url:{}", file.getName(), url);
            return url;
        } else {
            log.info("上传文件{}到cos失败,key:{},putObjectResult:{}", file.getName(), key, putObjectResult);
            return null;
        }
    }

}
