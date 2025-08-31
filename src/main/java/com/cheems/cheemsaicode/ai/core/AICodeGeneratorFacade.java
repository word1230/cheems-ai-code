package com.cheems.cheemsaicode.ai.core;

import cn.hutool.captcha.generator.CodeGenerator;
import com.cheems.cheemsaicode.ai.AICodeGeneratorService;
import com.cheems.cheemsaicode.ai.model.HtmlCodeResult;
import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum.MULTI_FILE;
import static com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum.HTML;

/**
 * ai生成代码门面类
 */
@Service
public class AICodeGeneratorFacade {

    @Resource
    private AICodeGeneratorService aiCodeGeneratorService;


    /**
     * 单html模式调用ai， 将生成文件写入到磁盘
     */
    public File generateAndSaveCode(String userMessage, AIGenTypeEnum genTypeEnum) {
       // 校验参数
        ThrowUtils.throwIf(genTypeEnum == null , ErrorCode.SYSTEM_ERROR,"生成类型为空");

        return  switch (genTypeEnum){
            case HTML -> generateHtmlCode(userMessage);
            case MULTI_FILE -> generateMultiFileCode(userMessage);
            default -> {
                String errorMsg = "不支持生成的类型"+ genTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,errorMsg);
            }
        };

    }

    private File generateHtmlCode(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return  CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }
    private File generateMultiFileCode(String userMessage) {
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return  CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
    }



}
