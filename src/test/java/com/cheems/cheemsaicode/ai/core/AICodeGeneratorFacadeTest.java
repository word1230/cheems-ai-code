package com.cheems.cheemsaicode.ai.core;

import com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AICodeGeneratorFacadeTest {

    @Resource
    private AICodeGeneratorFacade facade;

    @Test
    void generateAndSaveCode() {
        File file = facade.generateAndSaveCode("做一个代办工具", AIGenTypeEnum.HTML);
        Assertions.assertNotNull(file);
    }
    @Test
    void generateMultiFileSaveCode(){
        File file = facade.generateAndSaveCode("做一个代办工具,风格赛博朋克", AIGenTypeEnum.MULTI_FILE);
        Assertions.assertNotNull(file);
    }
}