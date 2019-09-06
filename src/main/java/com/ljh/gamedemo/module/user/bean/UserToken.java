package com.ljh.gamedemo.module.user.bean;

import lombok.Data;

/**
 * 玩家登录token信息
 */
@Data
public class UserToken {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户token信息
     */
    private String token;

}
