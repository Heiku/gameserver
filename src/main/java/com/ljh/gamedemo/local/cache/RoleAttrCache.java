package com.ljh.gamedemo.local.cache;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.dto.RoleAttr;

import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/23
 *
 * 本地存储玩家角色的 增益属性值
 */
public class RoleAttrCache {

    private static Map<Long, RoleAttr> roleAttrMap = Maps.newConcurrentMap();

    public static Map<Long, RoleAttr> getRoleAttrMap() {
        return roleAttrMap;
    }
}
