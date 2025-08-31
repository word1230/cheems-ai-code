package com.cheems.cheemsaicode.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Description("生成多个代码文件的结果")
@Data
public class MultiFileCodeResult {


    @Description("HTML代码")
    private String htmlCode;

    @Description("CSS代码")
    private String cssCode;

    @Description("JAVASCRIPT 代码")
    private String javascriptCode;

    @Description("生成代码的描述")
    private String description;

}
