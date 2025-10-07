package com.cheems.cheemsaicode.ai.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.cheems.cheemsaicode.ai.model.HtmlCodeResult;
import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;

import  java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 代码文件写入
 */

public class CodeFileSaver {

    //文件保存目录
    private  static  final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir")+"/tmp/code_output";

    /**
     * 保存htmlCOdeResult
     */
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult){
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath,"index.html",htmlCodeResult.getHtmlCode());
        return  new File(baseDirPath);
    }




    /**
     * 保存MultiFIleCodeResult
     */

    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult){
        String baseDir = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDir,"index.html",multiFileCodeResult.getHtmlCode());
        writeToFile(baseDir,"style.css",multiFileCodeResult.getCssCode());
        writeToFile(baseDir,"script.js",multiFileCodeResult.getJavascriptCode());
        return  new File(baseDir);
    }


    /**
     * 创建目录
     * @param bizType
     * @return
     */
    private  static  String buildUniqueDir(String bizType){
        String uniqueDir = StrUtil.format("{}_{}",bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDir;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     * @param dirPath
     * @param fileName
     * @param content
     */
    private  static  void writeToFile(String dirPath , String fileName , String content){
        String filePath = dirPath + File.separator + fileName;

        FileUtil.writeString(content,filePath, StandardCharsets.UTF_8);
    }


}
