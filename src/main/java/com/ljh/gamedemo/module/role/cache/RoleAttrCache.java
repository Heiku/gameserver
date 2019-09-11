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
 * 本地存储玩家角色的 增益属性值
 *
 * @Author: Heiku
 * @Date: 2019/7/23
 */
public class RoleAttrCache {


    /**
     * 本地玩家属性缓存 (roleId, RoleAttr)
     */
    private static Map<Long, RoleAttr> roleAttrMap = Maps.newConcurrentMap();


    /**
     * AttrDao
     */
    private static RoleAttrDao attrDao;

    static {
        attrDao = SpringUtil.getBean(RoleAttrDao.class);
    }

    public static Map<Long, RoleAttr> getRoleAttrMap() {
        return roleAttrMap;
    }


    /**
     * 读取玩家的属性信息
     */
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
