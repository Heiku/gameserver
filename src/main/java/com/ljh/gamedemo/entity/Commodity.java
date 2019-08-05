package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * @Author: Heiku
 * @Date: 2019/8/2
 *
 * 商品
 */

@Data
public class Commodity {

    /**
     * 商品id （物品id、装备id）
     */
    private Long id;

    /**
     * 商品类型 （物品 / 装备）
     */
    private Integer type;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 概率
     */
    private Double probability;

    /**
     * 限制购买数量
     */
    private Integer limit;


    /**
     * 装备
     */
    private Equip equip;

    private Items items;
}
