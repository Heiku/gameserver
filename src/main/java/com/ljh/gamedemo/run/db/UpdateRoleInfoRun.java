package com.ljh.gamedemo.run.db;

import com.ljh.gamedemo.module.role.dao.UserRoleDao;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 更新玩家的属性
 *
 * @Author: Heiku
 * @Date: 2019/8/13
 */

@Slf4j
public class UpdateRoleInfoRun implements Runnable {

    // 更新的角色信息
    private Role role;

    // 注入roleDao
    private UserRoleDao userRoleDao = SpringUtil.getBean(UserRoleDao.class);

    public UpdateRoleInfoRun(Role role){
        this.role = role;
    }

    @Override
    public void run() {
        // cache
        // update idRoleMap
        LocalUserMap.idRoleMap.put(role.getRoleId(), role);

        // update siteRoleMap
        List<Role> siteRoleList = LocalUserMap.siteRolesMap.get(role.getSiteId());
        for (int i = 0; i < siteRoleList.size(); i++) {
            if (siteRoleList.get(i).getRoleId().longValue() == role.getRoleId()){
                siteRoleList.set(i, role);
                break;
            }
        }

        LocalUserMap.siteRolesMap.put(role.getSiteId(), siteRoleList);

        // db
        int n = userRoleDao.updateRoleSiteInfo(role);
        log.info("Database update role where role name = " + role.getName() + " ,row = " + n);
    }
}
