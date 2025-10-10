package com.cheems.cheemsaicode.ai.core;

import com.cheems.cheemsaicode.ai.AICodeGeneratorService;
import com.cheems.cheemsaicode.ai.AiCodeGeneratorServiceFactory;
import com.cheems.cheemsaicode.ai.core.message.AiResponseMessage;
import com.cheems.cheemsaicode.ai.core.message.ToolExecutedMessage;
import com.cheems.cheemsaicode.ai.core.message.ToolRequestMessage;
import com.cheems.cheemsaicode.ai.core.parser.CodeParserExecutor;
import com.cheems.cheemsaicode.ai.core.saver.CodeFileSaverExecutor;
import com.cheems.cheemsaicode.ai.model.HtmlCodeResult;
import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import com.cheems.cheemsaicode.utils.ThrowUtils;

import cn.hutool.json.JSONUtil;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

import static com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum.*;

/**
 * ai生成代码门面类
 */
@Slf4j
@Service
public class AICodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;


    /**
     * 单html模式调用ai， 将生成文件写入到磁盘
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum genTypeEnum, Long appId) {
        // 校验参数
        ThrowUtils.throwIf(genTypeEnum == null, ErrorCode.SYSTEM_ERROR, "生成类型为空");

        AICodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId,genTypeEnum );

        return switch (genTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield  CodeFileSaverExecutor.saveFiles(htmlCodeResult, HTML,appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield  CodeFileSaverExecutor.saveFiles(multiFileCodeResult, MULTI_FILE,appId);
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
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum genTypeEnum, Long appId) {
        // 校验参数
        ThrowUtils.throwIf(genTypeEnum == null, ErrorCode.SYSTEM_ERROR, "生成类型为空");
        AICodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId,genTypeEnum);


        return switch (genTypeEnum) {
            case HTML -> {
                Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield  processCodeStream(result, HTML,appId);
            }
            case MULTI_FILE -> {
                Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield  processCodeStream(result, MULTI_FILE,appId);
            }
            case VUE_PROJECT -> {
                TokenStream result = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                yield  processTokenStream(result);
            }
            default -> {
                String errorMsg = "不支持生成的类型" + genTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMsg);
            }
        };

    }

    private Flux<String> processTokenStream(TokenStream tokenStream) {
      return Flux.create(sink ->{
             tokenStream
             .onPartialResponse((String partialResponse)->{
                  AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                  sink.next(JSONUtil.toJsonStr(aiResponseMessage));
             })
             .onPartialToolExecutionRequest((index,partialToolExecutionRequest )->{
                ToolRequestMessage toolRequestMessage = new ToolRequestMessage(partialToolExecutionRequest);
                sink.next(JSONUtil.toJsonStr(toolRequestMessage));
             })
             .onToolExecuted((ToolExecution toolExecution)->{
                ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
             })
             .onCompleteResponse((ChatResponse ChatResponse) ->{
                sink.complete();
             })
             .onError((Throwable throwable)->{
                throwable.printStackTrace();
                sink.error(throwable);
             }).start();;
        });
    }


    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum genType, Long appId) {

        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        Object parseCode = CodeParserExecutor.parseCode(codeBuilder.toString(), genType);
                        File file = CodeFileSaverExecutor.saveFiles(parseCode, genType,appId);
                        log.info("保存成功：路径为：" + file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败： {}", e.getMessage());
                    }
                });
    }

}
