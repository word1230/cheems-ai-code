package com.cheems.cheemsaicode.ai;

import com.cheems.cheemsaicode.ai.model.HtmlCodeResult;
import com.cheems.cheemsaicode.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;

/**
 * ai服务类
 */
public interface AICodeGeneratorService {

    /**
     * 生成html代码
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt" )
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 生成多文件代码
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);
}
