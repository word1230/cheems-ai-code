package com.cheems.cheemsaicode.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class WebScreenshotUtilsTest {



    @Test
    void testSaveWebPagescreenshot() {
        String webUrl = "http://localhost/ahrAFN/#/";
        String screenshotPath = WebScreenshotUtils.saveWebPagescreenshot(webUrl);
        log.info("截图路径:{}",screenshotPath);
    }
}
