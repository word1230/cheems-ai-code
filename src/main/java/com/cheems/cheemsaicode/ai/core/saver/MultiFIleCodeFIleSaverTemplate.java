package com.cheems.cheemsaicode.ai.core.saver;

import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import opennlp.tools.util.StringUtil;

public class MultiFIleCodeFIleSaverTemplate extends  CodeFIleSaverTemplate<MultiFileCodeResult>{
    @Override
    public CodeGenTypeEnum getAIGenType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    public void saveFiles(MultiFileCodeResult result, String path) {
        writeToFile(path,"index.html",result.getHtmlCode());
        if (result.getCssCode() != null) {
            writeToFile(path,"style.css",result.getCssCode());
        }
        if (result.getJavascriptCode() != null) {
            writeToFile(path,"script.js",result.getJavascriptCode());
        }
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        if(StringUtil.isEmpty(result.getHtmlCode())){
           throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HtmlCode 不能为空");
        }
    }
}
