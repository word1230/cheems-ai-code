package com.cheems.cheemsaicode.service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.manager.CosManager;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import com.cheems.cheemsaicode.utils.WebScreenshotUtils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScreenshotService {

    @Resource
    private  CosManager cosManager;


    /**
     * 生成并上传截图到cos
     * @param webUrl
     * @return
     */

    public String generateAndUploadScreenshot(String webUrl){
        ThrowUtils.throwIf(webUrl == null ,ErrorCode.SYSTEM_ERROR,"webUrl不能为空" );
        // 进行本地截图并返回截图文件路径
        String localScreenshotPath = WebScreenshotUtils.saveWebPagescreenshot(webUrl);
        ThrowUtils.throwIf(localScreenshotPath == null,ErrorCode.SYSTEM_ERROR,"截图失败" );
        try {
            // 上传截图文件到cos并返回url
            String cosUrl = uploadScreenshotAndReturnUrl(localScreenshotPath);
            ThrowUtils.throwIf(StrUtil.isBlank(cosUrl),ErrorCode.SYSTEM_ERROR,"上传截图失败" );
            log.info("网页截图并上传到cos成功,{}->{}",webUrl,cosUrl);
            return cosUrl;
        }finally{
            //清除本地文件
            cleanLocalFile(localScreenshotPath);
        }

        
    }



     /**
     * 清理本地截图文件及其父目录
     * 
     * @param localScreenshotPath 本地截图文件的路径
     */
    private void cleanLocalFile(String localScreenshotPath) {
        try {
            // 创建文件对象
            File file = new File(localScreenshotPath);
            // 检查文件是否存在
            if(file.exists()){
                // 获取文件的父目录
                File parentFile = file.getParentFile();
                //  删除父目录及目录下所有文件（递归删除）
                FileUtil.del(parentFile);
            }
            // 记录删除成功的日志信息
            log.info("本地截图文件{}删除成功",localScreenshotPath);
        } catch (Exception e) {
            // 记录删除失败的日志信息，包括异常堆栈
            log.info("删除截图文件{}失败",localScreenshotPath,e);
        }
    }


    private String uploadScreenshotAndReturnUrl(String localScreenshotPath) {

        if(StrUtil.isBlank(localScreenshotPath)){
            return null;
        }

        File screenshotFile = new File(localScreenshotPath);
        if(!screenshotFile.exists()){
            log.info("截图文件不存在：{}",localScreenshotPath);
            return null;
        }
        String fileName = UUID.randomUUID().toString().substring(0, 8) + "_compressed.jpg";
        String cosKey = generateScreenshotKey(fileName);
        return cosManager.uploadFile(cosKey, screenshotFile);
    }


    private String generateScreenshotKey(String fileName) {
        String dataePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return  String.format("/screenshots/%s/%s",dataePath,fileName);
    }

    
}
