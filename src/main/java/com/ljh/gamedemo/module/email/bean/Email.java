package com.ljh.gamedemo.module.email.bean;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Heiku
 * @Date: 2019/8/7
 *
 * 邮件信息实体类
 */

@Data
public class Email {

    /**
     * 邮件id
     */
    private long id;

    /**
     * 发件人
     */
    private long fromId;

    /**
     * 接收人
     */
    private long toRoleId;

    /**
     * 主题
     */
    private String theme;

    /**
     * 文本
     */
    private String content;


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
