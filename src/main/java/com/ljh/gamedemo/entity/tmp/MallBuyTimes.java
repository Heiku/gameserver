package com.ljh.gamedemo.entity.tmp;

import lombok.Data;

/**
 * @Author: Heiku
 * @Date: 2019/8/5
 *
 * 记录某样玩家购买商品的次数
 */

@Data
public class MallBuyTimes {

    // 商品id
    private Long cId;

    // 记录购买次数
    private Integer times = 0;
}
