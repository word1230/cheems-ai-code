package com.cheems.cheemsaicode.ai.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeFIleSaverTemplate<T> {


    //文件保存目录
    private  static  final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir")+"/tmp/code_output";

    public final File saveCode(T result){
        //1. 校验参数
        validateInput(result);

        // 2. 创建 目录
        String path = buildUniqueDir();

        //3. 保存到文件
        saveFiles(result, path);

        //4。返回文件对象
        return  new File((path));
    }


    /**
     * 创建目录
     * @return
     */
    private    String buildUniqueDir(){
        AIGenTypeEnum aiGenType = getAIGenType();
        String uniqueDir = StrUtil.format("{}_{}",aiGenType.getValue(), IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDir;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    public abstract AIGenTypeEnum getAIGenType();


    protected void validateInput(T result){
        if(result == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
    }


    public abstract void saveFiles(T result, String path);

    /**
     * 写入单个文件
     * @param dirPath
     * @param fileName
     * @param content
     */
    protected   static  void writeToFile(String dirPath , String fileName , String content){
        String filePath = dirPath + File.separator + fileName;

        FileUtil.writeString(content,filePath, StandardCharsets.UTF_8);
    }
}
