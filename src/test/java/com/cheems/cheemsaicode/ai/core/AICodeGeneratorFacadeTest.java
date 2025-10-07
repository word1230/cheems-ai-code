package com.cheems.cheemsaicode.ai.core;

import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@SpringBootTest
class AICodeGeneratorFacadeTest {

    @Resource
    private AICodeGeneratorFacade facade;

    @Test
    void generateAndSaveCode() {
        File file = facade.generateAndSaveCode("做一个代办工具", CodeGenTypeEnum.HTML,1L);
        Assertions.assertNotNull(file);
    }
    @Test
    void generateMultiFileSaveCode(){
        File file = facade.generateAndSaveCode("做一个代办工具,风格赛博朋克", CodeGenTypeEnum.MULTI_FILE,1L);
        Assertions.assertNotNull(file);
    }

    @Test
    void generateAndSaveCodeStream() {
        Flux<String> codeStream = facade.generateAndSaveCodeStream("做一个代办工具", CodeGenTypeEnum.HTML,1L);
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
        String join = String.join("", result);
        Assertions.assertNotNull(join);
    }


    @Test
    void generateMultiFileAndSaveCodeStream() {
        Flux<String> codeStream = facade.generateAndSaveCodeStream("做一个代办工具", CodeGenTypeEnum.MULTI_FILE,1L);
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
        String join = String.join("", result);
        Assertions.assertNotNull(join);
    }
}