package com.cheems.cheemsaicode.ai.core.saver;

import com.cheems.cheemsaicode.ai.model.HtmlCodeResult;
import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import com.cheems.cheemsaicode.ai.model.enums.AIGenTypeEnum;
import com.cheems.cheemsaicode.exception.BusinessException;
import com.cheems.cheemsaicode.exception.ErrorCode;

import java.io.File;

public class CodeFileSaverExecutor {

    private  static  HtmlCodeFileSaverTemplate htmlCodeFileSaverTemplate = new HtmlCodeFileSaverTemplate();
    private  static  MultiFIleCodeFIleSaverTemplate multiFIleCodeFIleSaverTemplate = new MultiFIleCodeFIleSaverTemplate();
    public static File saveFiles(Object result, AIGenTypeEnum genType){
        return switch (genType){
            case HTML ->
                htmlCodeFileSaverTemplate.saveCode((HtmlCodeResult) result);
            case MULTI_FILE ->
                multiFIleCodeFIleSaverTemplate.saveCode((MultiFileCodeResult) result);
            default ->
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持的生成类型"+genType);
        };
    }
}
