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

    /**
     * 队伍id
     */
    private Long id;

    /**
     * 队伍队长
     */
    private Long leader;

    /**
     * 成员列表
     */
    private List<Long> members = new ArrayList<>();
}
