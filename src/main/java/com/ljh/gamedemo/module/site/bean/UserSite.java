package com.ljh.gamedemo.module.site.bean;

import lombok.Data;

/**
 * 场景实体关联
 */
@Data
public class UserSite {

    /**
     * 玩家id
     */
    private Long userId;

    /**
     * 位置id
     */
    private Integer siteId;
}
