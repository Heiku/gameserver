package com.ljh.gamedemo.module.user.bean;

import lombok.Data;

/**
 * 用户实体类
 */
@Data
public class User {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;
}
