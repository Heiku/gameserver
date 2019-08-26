package com.ljh.gamedemo.common;

/**
 * 公会成员的职位类型
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */
public enum MemberType {

    ORDINARY(1, "普通成员"),
    ELITE(2, "精英"),
    ELDER(3, "长老"),
    VICE_PRESIDENT(4, "副会长"),
    PRESIDENT(5, "会长");

    int code;
    String name;

    MemberType(int _code, String _name){
        this.code = _code;
        this.name = _name;
    }
}
