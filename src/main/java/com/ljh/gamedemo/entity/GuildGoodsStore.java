package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * 公会物品库存
 *
 * @Author: Heiku
 * @Date: 2019/8/27
 */

@Data
public class GuildGoodsStore {

    /**
     * id
     */
    private Long id;

    /**
     * 公会id
     */
    private Long guildId;

    /**
     * 物品id
     */
    private Long goodsId;

    /**
     * 物品数量
     */
    private Integer num;
}
