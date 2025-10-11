package com.cheems.cheemsaicode.service.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.service.ProjectDownloadService;
import com.cheems.cheemsaicode.utils.ThrowUtils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProjectDownloadServiceImpl implements ProjectDownloadService {

    /**
     * 需要过滤的文件和目录名称
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules",
            ".git",
            "target",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode");

    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log",
            ".tmp",
            ".cache");


    private boolean isPathAllowed(Path projectRoot,Path fullPath){
        //获取相对路径 
        Path relativePath = projectRoot.relativize(fullPath);
        //检查路径中的每一部分
        for(Path part : relativePath){
            String partName =part.toString();
            if(IGNORED_NAMES.contains(partName)){
                return false;
            }
            //检查扩展名
            if(IGNORED_EXTENSIONS.stream().anyMatch(partName::endsWith)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response) {

        // 校验参数
        ThrowUtils.throwIf(StrUtil.isBlank(projectPath), ErrorCode.PARAMS_ERROR, "项目路径不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(downloadFileName), ErrorCode.PARAMS_ERROR, "下载文件名不能为空");
        File projectDir = new File(projectPath);
        ;
        ThrowUtils.throwIf(!projectDir.exists(), ErrorCode.PARAMS_ERROR, "项目目录不存在");
        ThrowUtils.throwIf(!projectDir.isDirectory(), ErrorCode.PARAMS_ERROR, "项目路径不是目录");
        log.info("开始打包下载项目：{}->{}.zip", projectPath, downloadFileName);
        // 设置响应头

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + ".zip\"");
        // 过滤文件
        FileFilter filter = file -> isPathAllowed(projectDir.toPath(),file.toPath());
        try{
            ZipUtil.zip(response.getOutputStream(),StandardCharsets.UTF_8,false,filter,projectDir);
            log.info("项目打包下载完成：{}",downloadFileName);
        }catch(IOException e){
            log.error("下载项目压缩包失败",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"下载项目压缩包失败");
        }
    }

}
