package com.ljh.gamedemo.local.cache;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.dao.RoleAttrDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.dto.RoleAttr;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.util.SpringUtil;

import java.util.List;
import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/23
 *
 * 本地存储玩家角色的 增益属性值
 */
public class RoleAttrCache {

    private static Map<Long, RoleAttr> roleAttrMap = Maps.newConcurrentMap();

    private static RoleAttrDao attrDao;

    static {
        attrDao = SpringUtil.getBean(RoleAttrDao.class);
    }

    public static Map<Long, RoleAttr> getRoleAttrMap() {
        return roleAttrMap;
    }

    public static void readBDAttr(){
        List<RoleAttr> attrList = attrDao.selectAllAttr();
        for (RoleAttr attr : attrList) {
            roleAttrMap.put(attr.getRoleId(), attr);

            Role role = LocalUserMap.idRoleMap.get(attr.getRoleId());
            role.setHp(role.getMaxHp() + attr.getHp());
            LocalUserMap.idRoleMap.put(role.getRoleId(), role);
            LocalUserMap.userRoleMap.put(role.getUserId(), role);
        }
    }
}
