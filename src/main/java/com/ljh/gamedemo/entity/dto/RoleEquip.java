package com.ljh.gamedemo.entity.dto;

import lombok.Data;

/**
 * @Author: Heiku
 * @Date: 2019/7/17
 */
@Data
public class RoleEquip {

    private Long id;

    private Long roleId;

    private Long equipId;

    private Integer durability;

    private Integer state;
}
