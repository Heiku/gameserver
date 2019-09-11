package com.ljh.gamedemo.module.goods.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 背包中的物品，通用类
 *
 * @Author: Heiku
 * @Date: 2019/8/7
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    /**
     * 物品id
     */
    private Long gid;

    /**
     * 物品类型
     */
    private Integer type;
}
