package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Site {
    private int id;
    private String name;
    private String cName;

    public List<String> next = new ArrayList<>();

}
