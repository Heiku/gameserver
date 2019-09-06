package com.ljh.gamedemo.module.role.cache;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.role.dao.RoleAttrDao;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.bean.RoleAttr;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
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
            role.setHp(role.getHp() + attr.getHp());
            LocalUserMap.idRoleMap.put(role.getRoleId(), role);
            LocalUserMap.userRoleMap.put(role.getUserId(), role);
        }
    }
}
