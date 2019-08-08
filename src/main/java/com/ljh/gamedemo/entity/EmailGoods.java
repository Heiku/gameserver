package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 *
 * 邮件与物品关联，存储邮件中的物品信息
 */

@Data
public class EmailGoods {

    /**
     * id
     */
    private Long id;

    /**
     * 物品id
     */
    private Long gid;

    /**
     * 邮件id
     */
    private Long eid;

    /**
     * 物品数量
     */
    private Integer num;
}
