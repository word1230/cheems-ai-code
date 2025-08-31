package com.cheems.cheemsaicode.ai;

import com.cheems.cheemsaicode.ai.model.HtmlCodeResult;
import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AICodeGeneratorServiceTest {


    @Resource
    private AICodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {

        HtmlCodeResult s = aiCodeGeneratorService.generateHtmlCode("做一个代办工具");
        System.out.println(s.toString());
        Assertions.assertNotNull(s);


    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult s = aiCodeGeneratorService.generateMultiFileCode("做一个代办工具");
        System.out.println(s.toString());
        Assertions.assertNotNull(s);

    }


}