package com.ljh.gamedemo.module.group.bean;

import com.ljh.gamedemo.module.role.bean.Role;
import lombok.Data;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/1
 *
 * 团队实体
 */

@Data
public class Team {

    private Long id;

    private String name;

    private List<Role> roleList;
}
