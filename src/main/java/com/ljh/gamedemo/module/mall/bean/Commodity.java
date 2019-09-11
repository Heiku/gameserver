package com.ljh.gamedemo.module.mall.bean;

import com.ljh.gamedemo.module.items.bean.Items;
import com.ljh.gamedemo.module.equip.bean.Equip;
import lombok.Data;

/**
 * 商品实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/2
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
     * 装备信息
     */
    private Equip equip;

    /**
     * 物品信息
     */
    private Items items;
}
