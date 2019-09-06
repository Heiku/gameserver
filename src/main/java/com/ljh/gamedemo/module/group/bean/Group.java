package com.ljh.gamedemo.module.group.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 组队实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/14
 */

@Data
public class Group {

    private Long id;

    private Long leader;

    private List<Long> members = new ArrayList<>();
}
