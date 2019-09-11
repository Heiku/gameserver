package com.ljh.gamedemo.module.site.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景实体类
 */

@Data
public class Site {

    /**
     * 场景id
     */
    private int id;

    /**
     * 场景名称
     */
    private String name;

    /**
     * 场景中文名
     */
    private String cName;

    /**
     * 相邻的场景信息
     */
    public List<String> next = new ArrayList<>();

}
