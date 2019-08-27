package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 公会物品记录
 *
 * @Author: Heiku
 * @Date: 2019/8/27
 */

@Data
public class GuildGoodsRecord {

    /**
     * 记录id
     */
    private Long id;

    /**
     * 关联的玩家id
     */
    private Long roleId;

    /**
     * 公会id
     */
    private Long guildId;

    /**
     * 物品id
     */
    private Long goodsId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 类型：1：取出，2：存入
     */
    private Integer type;

    /**
     * 时间
     */
    private Date createTime;
}
