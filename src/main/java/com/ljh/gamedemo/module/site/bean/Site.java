package com.ljh.gamedemo.module.site.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景实体类
 */

@Data
public class Site {
    private int id;
    private String name;
    private String cName;

    public List<String> next = new ArrayList<>();

}
