package com.cheems.cheemsaicode.service;


import jakarta.servlet.http.HttpServletResponse;



/** 
 * 项目下载服务
 */
public interface ProjectDownloadService {

    /**
     * 下载项目代码（zip格式）
     * @param projectPath
     * @param downloadFileName
     * @param response
     */
    void downloadProjectAsZip(String projectPath,String downloadFileName ,HttpServletResponse response);


}
