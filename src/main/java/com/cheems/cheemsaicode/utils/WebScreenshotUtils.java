package com.cheems.cheemsaicode.utils;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebScreenshotUtils {

    private static final WebDriver webDriver; 

    static{ 
        final int DEFAULT_WIDTH = 1024;
        final int DEFAULT_HEIGHT = 768;
        webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    
    public  static String saveWebPagescreenshot(String webUrl){
        if(StrUtil.isBlank(webUrl)){
            log.error("webUrl不能为空");
            return null;
        }
        try{
            //创建临时目录
            String rootPath = System.getProperty("user.dir")+File.separator+"temp"+File.separator+"screenshots" +File.separator+UUID.randomUUID().toString().substring(0,8);
            FileUtil.mkdir(rootPath);
            // 图片后缀
            final String IMAGE_SUFFIX = ".png";

            //原始截图文件路径
            String originalImagePath = rootPath+File.separator+RandomUtil.randomNumbers(5)+IMAGE_SUFFIX;
            //访问网页
            webDriver.get(webUrl);

            // 等待页面加载完毕
            waitForPageLoad(webDriver);

            // 截图
             byte[] screenshot = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.BYTES);
           //保存原始图片
             saveImage(screenshot, originalImagePath);

            //压缩图片
            final String COMPARESSION_SUFFIX = "_compressed.png";
            String compressedImagePath = originalImagePath.replace(IMAGE_SUFFIX, COMPARESSION_SUFFIX);
            compressImage(originalImagePath,compressedImagePath);
            log.info("保存压缩文件成功");
            //删除原始图片，只保留压缩后的图片
            FileUtil.del(originalImagePath);
            return compressedImagePath;
        }catch(Exception e){
            log.error("保存网页截图失败", e);
            return null;
        }
    }
    private static void compressImage(String originalImagePath, String compressedImagePath) {

        final float COMPRESSION_QUALITY =0.3f;
        try {
            ImgUtil.compress(FileUtil.file(originalImagePath), FileUtil.file(compressedImagePath), COMPRESSION_QUALITY);
        } catch (Exception e) {
            log.error("压缩图片失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩图片失败"+originalImagePath);
        }
    }
    private static void saveImage(byte[] screenshot, String originalImagePath) {
     
        try {
             FileUtil.writeBytes(screenshot, originalImagePath);
        } catch (Exception e) {
           
            log.error("保存图片失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存图片失败"+originalImagePath);
        }
    }
    private static void waitForPageLoad(WebDriver driver) {
        try {
         WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
         webDriverWait.until(webdriver ->((JavascriptExecutor) webdriver).executeScript("return document.readyState").equals("complete"));
         Thread.sleep(2000);
         log.info("页面加载完毕");
        } catch (Exception e) {
            log.error("等待页面加载超时", e);
        }
    }



     /**
     * 初始化 Chrome 浏览器驱动
     */
    private static WebDriver initChromeDriver(int width, int height) {
        try {
            // 自动管理 ChromeDriver
            System.setProperty("wdm.chromeDriverMirrorUrl", "https://registry.npmmirror.com/binary.html?path=chromedriver");
            WebDriverManager.chromedriver().useMirror().setup();
            // 配置 Chrome 选项
            ChromeOptions options = new ChromeOptions();
            // 无头模式
            options.addArguments("--headless");
            // 禁用GPU（在某些环境下避免问题）
            options.addArguments("--disable-gpu");
            // 禁用沙盒模式（Docker环境需要）
            options.addArguments("--no-sandbox");
            // 禁用开发者shm使用
            options.addArguments("--disable-dev-shm-usage");
            // 设置窗口大小
            options.addArguments(String.format("--window-size=%d,%d", width, height));
            // 禁用扩展
            options.addArguments("--disable-extensions");
            // 设置用户代理
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            // 创建驱动
            WebDriver driver = new ChromeDriver(options);
            // 设置页面加载超时
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // 设置隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;
        } catch (Exception e) {
            log.error("初始化 Chrome 浏览器失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
        }
    }



}
