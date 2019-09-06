package com.ljh.gamedemo.module.items.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 道具实体类
 *
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@Data
public class Items {

    /**
     * 道具id
     */
    private Long itemsId;

    /**
     * 道具名
     */
    private String name;

    /**
     * 道具类型
     */
    private Integer type;

    /**
     * 道具数量
     */
    @EqualsAndHashCode.Exclude
    private Integer num;

    /**
     * 道具持续时间
     */
    private Integer sec;

    /**
     * 道具描述
     */
    private String desc;

    /**
     * 道具增益效果
     */
    private Integer up;

    /**
     * 最低交易额
     */
    private Integer minTrans;
}
