package com.cheems.cheemsaicode.ai.model;

import com.cheems.cheemsaicode.ai.model.enums.CodeGenTypeEnum;

import lombok.Data;

@Data
public class AiCodeGenAppParam {

    private String name;
    private CodeGenTypeEnum codeGenTypeEnum;
}
