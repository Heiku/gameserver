package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.User;
import com.ljh.gamedemo.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalUserMap {

    @Autowired
    private static UserRoleDao userRoleDao;

    static {
        userRoleDao = SpringUtil.getBean(UserRoleDao.class);
    }

    // 存放当前在线的的玩家 (userId, User)
    public static Map<Long, User> userMap = Maps.newConcurrentMap();

    // 存放当前玩家的角色 (userId, role)
    public static Map<Long, Role> userRoleMap = Maps.newConcurrentMap();

    // 存放当前玩家的角色位置 (siteId, List<Role>)
    public static Map<Integer, List<Role>> siteRolesMap = Maps.newConcurrentMap();

    // 存放玩家的角色id，只用于初始化
    public static Map<Long, Role> idRoleMap = Maps.newConcurrentMap();


    // 读取数据库，获取role的位置信息
    public static void readSiteRoles(){
        List<Role> roles = userRoleDao.selectAllRole();
        for (Role role : roles){
            List<Role> list = null;

            // 获取场景ID
            int siteId = role.getSiteId();

            list = siteRolesMap.get(siteId);
            if (list == null || list.isEmpty()){
                list = new ArrayList<>();
            }
            list.add(role);
            siteRolesMap.put(siteId, list);

            idRoleMap.put(role.getRoleId(), role);
        }
    }
    public static Map<Long, Role> getIdRoleMap() {
        return idRoleMap;
    }

    public static void setIdRoleMap(Map<Long, Role> idRoleMap) {
        LocalUserMap.idRoleMap = idRoleMap;
    }
}
