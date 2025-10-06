package com.cheems.cheemsaicode.ai.core.saver;

import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import opennlp.tools.util.StringUtil;

import java.io.File;

public class MultiFIleCodeFIleSaverTemplate extends  CodeFIleSaverTemplate<MultiFileCodeResult>{
    @Override
    public AIGenTypeEnum getAIGenType() {
        return AIGenTypeEnum.MULTI_FILE;
    }

    @Override
    public void saveFiles(MultiFileCodeResult result, String path) {
        writeToFile(path,"index.html",result.getHtmlCode());
        writeToFile(path,"style.css",result.getCssCode());
        writeToFile(path,"script.js",result.getJavascriptCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        if(StringUtil.isEmpty(result.getHtmlCode())){
           throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HtmlCode 不能为空");
        }
    }
}
