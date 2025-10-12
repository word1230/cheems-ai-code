package com.cheems.cheemsaicode.ai.core.builder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VueProjectBuilder {


    /**
     * 异步构建Vue项目
     * @param ProjectDir
     */

    public void buildVueProjectAsyn(String ProjectDir){
        Thread.ofVirtual().name("vue-builder-"+ System.currentTimeMillis()).start(()->{
            try {
                buildVueProject(ProjectDir);
            } catch (Exception e) {
                log.error("异步构建Vue项目失败:{}",ProjectDir,e);
            }
        });
    }



    /**
     * 构建Vue项目
     * @param ProjectDir
     * @return
     */

    public boolean buildVueProject(String ProjectDir){
        File ProjectDirFile = new File(ProjectDir);
        if(!ProjectDirFile.exists() || !ProjectDirFile.isDirectory()){
            log.error("项目目录不存在或不是目录:{}",ProjectDir);
            return false;
        }
        //检查是否package.json是否存在
        File packageJsonFile = new File(ProjectDirFile,"package.json");
        if(!packageJsonFile.exists()){
            log.error("项目目录{}不存在package.json文件",ProjectDir);
            return false;
        }
        log.info("开始构建Vue项目:{}",ProjectDir);
        if(!executeNpmInstall(ProjectDirFile)){
            log.error("npm install 失败");
            return false;
        }
        if(!executeNpmRunBuild(ProjectDirFile)){
            log.error("npm run build 失败");
            return false;
        }
        //验证dist目录是否存在
        File distDir = new File(ProjectDirFile,"dist");
        if(!distDir.exists() || !distDir.isDirectory()){
            log.error("项目目录{}不存在dist目录",ProjectDir);
            return false;
        }
        log.info("项目目录{}构建成功,dist目录存在",ProjectDir);
        return true;
    }


    /**
     * 执行npm install命令
     * @param ProjectDir
     * @return
     */
    private boolean executeNpmInstall(File ProjectDir){
        log.info("在目录{}执行npm install命令",ProjectDir.getAbsolutePath());
        String command = String.format("%s install", buildCommand("npm"));
        return executeCommand(ProjectDir,command,300);
    }

    /**
     * 执行npm run build命令
     * @param ProjectDir
     * @return
     */
    private boolean executeNpmRunBuild(File ProjectDir){
        log.info("在目录{}执行npm run build命令",ProjectDir.getAbsolutePath());
        String command = String.format("%s run build", buildCommand("npm"));
        return executeCommand(ProjectDir,command,300);
    }

      private String buildCommand(String baseCommand){
        if(isWindows()){
            return baseCommand + ".cmd";
        }else{
            return baseCommand;
        }
    }

    /**
     * 判断是否是Windows系统 
     * @return
     */

    private boolean isWindows(){
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    


    /**
     * 执行命令
     * @param workingDir
     * @param Command
     * @param timeoutSeconds
     * @return
     */
    private boolean executeCommand(File workingDir,String command,int timeoutSeconds){
        
        try {
            log.info("在目录{}执行命令:{}",workingDir.getAbsolutePath(),command);
            Process process = RuntimeUtil.exec(null,workingDir,command.split("\s+"));
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if(!finished){
                log.error("命令执行超时,超时时间:{}秒,强制终止进程",timeoutSeconds);
                process.destroyForcibly();
                return false;
            }
            int exitCode = process.exitValue();
            if(exitCode!=0){
                log.error("命令执行失败,退出码:{}",exitCode);
                return false;
            }else{
                log.info("命令执行成功,退出码:{}",exitCode);
                return true;
            }
        } catch (InterruptedException e) {
            log.error("命令执行失败{},错误信息:{}",command ,e.getMessage());
            return false;
        }
    }
}
