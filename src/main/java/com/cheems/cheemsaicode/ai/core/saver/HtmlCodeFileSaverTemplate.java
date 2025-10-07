package com.cheems.cheemsaicode.ai.core.saver;

import com.cheems.cheemsaicode.ai.model.HtmlCodeResult;
import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;
import opennlp.tools.util.StringUtil;

public class HtmlCodeFileSaverTemplate extends CodeFIleSaverTemplate<HtmlCodeResult>{
    @Override
    public CodeGenTypeEnum getAIGenType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    public void saveFiles(HtmlCodeResult result, String path) {
        writeToFile(path,"index.html",result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        if(StringUtil.isEmpty(result.getHtmlCode())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "htmlCode 不能为空");
        }
    }


}
