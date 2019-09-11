package com.ljh.gamedemo.module.mall.tmp;

import lombok.Data;

/**
 * 记录某样玩家购买商品的次数
 *
 * @Author: Heiku
 * @Date: 2019/8/5
 */

@Data
public class MallBuyTimes {

    /**
     * 商品id
     */
    private Long cId;

    /**
     * 记录购买次数 （初始值为 0 ）
     */
    private Integer times = 0;
}
