package com.ljh.gamedemo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Heiku
 * @Date: 2019/7/16
 *
 *
 * 道具实体类
 */

@Data
public class Items {

    private Long itemsId;

    private String name;

    private Integer type;

    @EqualsAndHashCode.Exclude
    private Integer num;

    private Integer sec;

    private String desc;

    private Integer up;
}
