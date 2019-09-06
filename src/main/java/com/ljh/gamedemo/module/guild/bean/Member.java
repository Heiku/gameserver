package com.ljh.gamedemo.module.guild.bean;

import lombok.Data;

/**
 * 公会成员实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */

@Data
public class Member {
    /**
     * 成员id
     */
    private Long roleId;

    /**
     * 所属公会id
     */
    private Long gid;

    /**
     * 职位
     */
    private Integer position;

    /**
     *  今日贡献值
     */
    private Integer todayCon;

    /**
     * 总贡献值
     */
    private Integer allCon;
}
