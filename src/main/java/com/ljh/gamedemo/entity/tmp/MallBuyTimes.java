package com.ljh.gamedemo.entity.tmp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
