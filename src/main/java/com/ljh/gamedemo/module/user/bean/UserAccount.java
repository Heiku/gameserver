package com.ljh.gamedemo.module.user.bean;

import lombok.Data;

/**
 * 用户账户实体类
 *
 * @Author: Heiku
 * @Date: 2019/9/23
 */

@Data
public class UserAccount {

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

    public UserAccount(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
}
