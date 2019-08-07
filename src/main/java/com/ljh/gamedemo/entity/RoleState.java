package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Heiku
 * @Date: 2019/8/6
 */

@Data
public class RoleState {

    private Long id;

    private Long roleId;

    private Date onlineTime;

    private Date offlineTime;
}
