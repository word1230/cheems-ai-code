package com.cheems.cheemsaicode.ai.core;

import cn.hutool.captcha.generator.CodeGenerator;
import com.cheems.cheemsaicode.ai.AICodeGeneratorService;
import com.cheems.cheemsaicode.ai.core.parser.CodeParserExecutor;
import com.cheems.cheemsaicode.ai.core.saver.CodeFileSaverExecutor;
import com.cheems.cheemsaicode.ai.model.HtmlCodeResult;
import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.utils.ThrowUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

import static com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum.MULTI_FILE;
import static com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum.HTML;

/**
 * ai生成代码门面类
 */
@Slf4j
@Service
public class AICodeGeneratorFacade {

    @Resource
    private AICodeGeneratorService aiCodeGeneratorService;


    /**
     * 单html模式调用ai， 将生成文件写入到磁盘
     */
    public File generateAndSaveCode(String userMessage, AIGenTypeEnum genTypeEnum) {
        // 校验参数
        ThrowUtils.throwIf(genTypeEnum == null, ErrorCode.SYSTEM_ERROR, "生成类型为空");

        return switch (genTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield  CodeFileSaverExecutor.saveFiles(htmlCodeResult, HTML);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield  CodeFileSaverExecutor.saveFiles(multiFileCodeResult, MULTI_FILE);
            }
            default -> {
                String errorMsg = "不支持生成的类型" + genTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMsg);
            }
        };

    }

    /**
     * 单html模式调用ai， 将生成文件写入到磁盘(流式)
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, AIGenTypeEnum genTypeEnum) {
        // 校验参数
        ThrowUtils.throwIf(genTypeEnum == null, ErrorCode.SYSTEM_ERROR, "生成类型为空");

        return switch (genTypeEnum) {
            case HTML -> {
                Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield  processCodeStream(result, HTML);
            }
            case MULTI_FILE -> {
                Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield  processCodeStream(result, MULTI_FILE);
            }
            default -> {
                String errorMsg = "不支持生成的类型" + genTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMsg);
            }
        };

    }


    private Flux<String> processCodeStream(Flux<String> codeStream, AIGenTypeEnum genType) {

        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        Object parseCode = CodeParserExecutor.parseCode(codeBuilder.toString(), HTML);
                        File file = CodeFileSaverExecutor.saveFiles(parseCode, HTML);
                        log.info("保存成功：路径为：" + file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败： {}", e.getMessage());
                    }
                });
    }


    private File generateHtmlCode(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }

    private File generateMultiFileCode(String userMessage) {
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
    }


}
