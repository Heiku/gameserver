package com.ljh.gamedemo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 *
 * 邮件与物品关联，存储邮件中的物品信息
 */

@Data
@NoArgsConstructor
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

    public EmailGoods(Long _gid, Integer _num){
        this.gid = _gid;
        this.num = _num;
    }
}
