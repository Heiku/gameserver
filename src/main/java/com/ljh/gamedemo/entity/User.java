package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * 用户实体类
 */
@Data
public class User {

    private Long userId;

    private String userName;

    private String password;
}
