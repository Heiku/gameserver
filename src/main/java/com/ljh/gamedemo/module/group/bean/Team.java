package com.ljh.gamedemo.module.group.bean;

import com.ljh.gamedemo.module.role.bean.Role;
import lombok.Data;

import java.util.List;

/**
 * 队伍实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/1
 */

@Data
public class Team {

    /**
     * 队伍id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 玩家列表
     */
    private List<Role> roleList;
}
