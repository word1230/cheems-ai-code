package com.cheems.cheemsaicode.ai.tools;

import com.cheems.cheemsaicode.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件写入工具类
 * 支持AI通过调用工具类写入文件
 */

@Slf4j
public class FileWriteTool {


    @Tool("写入文件到指定路径")
    public String writeFile(
            @P("文件的相对路径")String relativeFilePath,
            @P("要写入文件的内容")String content,
            @ToolMemoryId Long appId){
        try{
            //1. 根据相对路径创建基于appid 的项目目录
            Path path = Paths.get(relativeFilePath);
            if(!path.isAbsolute()){
                //如果是相对路径就创建基于appid的项目目录
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                path = projectRoot.resolve(relativeFilePath);
            }
            //2. 创建父目录（如果不存在）
            Path parentDir = path.getParent();
            if(parentDir != null){
                Files.createDirectories(parentDir);
            }
            //3. 写入文件
            Files.write(path, content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            log.info("写入文件成功：{}",path.toAbsolutePath());
            // 4. 返回结果
            return "文件写入成功" + relativeFilePath;
        }catch (Exception e){

            String errorMessage = "写入文件失败：" + relativeFilePath + "错误：" + e.getMessage();
            log.error(errorMessage,e);
            return errorMessage;
        }
    }


}
