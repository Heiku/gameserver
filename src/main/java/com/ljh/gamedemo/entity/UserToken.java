package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * 玩家登录token信息
 */
@Data
public class UserToken {

    private Long userId;

    private String token;

}
