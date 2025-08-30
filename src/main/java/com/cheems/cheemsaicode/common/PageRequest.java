package com.cheems.cheemsaicode.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数封装类
 */
@Data
public class PageRequest implements Serializable {

    /**
     * 页码
     */

    private int pageNum = 1;
    /**
     * 每页显示数量
     */
    private int pageSize = 10;
    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式
     */
    private String sortOrder = "descend";
}
