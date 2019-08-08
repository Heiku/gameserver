package com.ljh.gamedemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Heiku
 * @Date: 2019/8/7
 *
 * 背包中的物品，通用类
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    private Long gid;

    private Integer type;
}
