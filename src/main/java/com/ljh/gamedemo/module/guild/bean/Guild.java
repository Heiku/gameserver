package com.ljh.gamedemo.module.guild.bean;

import lombok.Data;

import java.util.List;

/**
 * 公会实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */

@Data
public class Guild {

    /**
     * 公会id
     */
    private Long id;

    /**
     * 公会名称
     */
    private String name;

    /**
     * 公告
     */
    private String bulletin;

    /**
     * 公会等级
     */
    private Integer level;

    /**
     * 公会人数
     */
    private Integer num;

    /**
     * 公会的最大人数值
     */
    private Integer maxNum;

    /**
     * 会长
     */
    private Long president;

    /**
     * 公会成员
     */
    private List<Member> members;
}
