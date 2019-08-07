package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Heiku
 * @Date: 2019/8/7
 */

@Data
public class Email {

    /**
     * 邮件id
     */
    private long id;

    /**
     * 背包物品id
     */
    private long gid;

    /**
     * 接收人
     */
    private long toRoleId;

    /**
     * 邮件
     */
    private Integer num;

    /**
     * 邮件装备：0：未领取， 1：领取
     */
    private Integer state;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 领取时间
     */
    private Date modifyTime;
}
