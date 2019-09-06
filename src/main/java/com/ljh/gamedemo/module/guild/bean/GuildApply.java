package com.ljh.gamedemo.module.guild.bean;

import lombok.Data;

import java.util.Date;

/**
 * 公会申请记录实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */

@Data
public class GuildApply {

    /**
     * 申请id
     */
    private Long id;

    /**
     * 玩家id
     */
    private Long roleId;

    /**
     * 公会id
     */
    private Long guildId;

    /**
     * 审批人
     */
    private Long approver;

    /**
     * 进度 0: 未审批，1，审批通过，2.审批拒绝
     */
    private Integer process;

    /**
     * 申请时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;
}
