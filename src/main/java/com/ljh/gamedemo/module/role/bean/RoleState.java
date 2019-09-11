package com.ljh.gamedemo.module.role.bean;

import lombok.Data;

import java.util.Date;

/**
 * 玩家上下线记录实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/6
 */

@Data
public class RoleState {

    /**
     * id
     */
    private Long id;

    /**
     * 玩家id
     */
    private Long roleId;

    /**
     * 在线时间
     */
    private Date onlineTime;

    /**
     * 离线时间
     */
    private Date offlineTime;
}
