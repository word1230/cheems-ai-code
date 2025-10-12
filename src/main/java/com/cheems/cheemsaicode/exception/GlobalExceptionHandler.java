package com.cheems.cheemsaicode.exception;

import cn.hutool.json.JSONUtil;
import com.cheems.cheemsaicode.common.BaseResponse;
import com.cheems.cheemsaicode.utils.ResultUtils;
import dev.langchain4j.exception.RateLimitException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> handleBusinessException( BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());

    }

    /**
     * 处理速率限制异常，返回流式响应格式的错误信息
     */
    @ExceptionHandler(RateLimitException.class)
    public Flux<ServerSentEvent<String>> handleRateLimitException(RateLimitException e) {
        log.error("RateLimitException", e);

        String errorMessage = "请求过于频繁，请稍后再试。API 限制：" + e.getMessage();
        Map<String, String> errorWrapper = Map.of("error", errorMessage);
        String jsonData = JSONUtil.toJsonStr(errorWrapper);

        return Flux.concat(
            Mono.just(ServerSentEvent.<String>builder()
                    .data(jsonData)
                    .build()),
            Mono.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("")
                    .build())
        );
    }

    /**
     * 处理流式响应中的其他运行时异常
     */
    @ExceptionHandler(value = {RuntimeException.class, Exception.class})
    public Object handleStreamingException(Exception e) {
        log.error("RuntimeException in streaming context", e);

        // 检查是否在流式响应上下文中（通过检查调用栈）
        StackTraceElement[] stackTrace = e.getStackTrace();
        boolean isStreamingContext = false;

        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains("reactor") ||
                element.getClassName().contains("AppController") ||
                element.getMethodName().contains("chatToGenCode")) {
                isStreamingContext = true;
                break;
            }
        }

        if (isStreamingContext) {
            // 返回流式响应格式的错误信息
            String errorMessage = "服务处理异常：" + e.getMessage();
            Map<String, String> errorWrapper = Map.of("error", errorMessage);
            String jsonData = JSONUtil.toJsonStr(errorWrapper);

            return Flux.concat(
                Mono.just(ServerSentEvent.<String>builder()
                        .data(jsonData)
                        .build()),
                Mono.just(ServerSentEvent.<String>builder()
                        .event("error")
                        .data("")
                        .build())
            );
        } else {
            // 返回普通响应格式的错误信息
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }
}
