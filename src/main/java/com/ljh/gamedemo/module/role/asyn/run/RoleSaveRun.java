package com.ljh.gamedemo.module.role.asyn.run;

import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.dao.UserRoleDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * 玩家数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class RoleSaveRun implements Runnable {

    /**
     * 玩家信息
     */
    private Role role;

    /**
     * 操作类型
     */
    private int type;

    /**
     * UserRoleDao
     */
    private UserRoleDao dao = SpringUtil.getBean(UserRoleDao.class);

    public RoleSaveRun(Role role, int type) {
        this.role = role;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertUserRole(role);
                log.info("insert into role, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateRoleSiteInfo(role);
                log.info("update role, affected rows: " + n);
                break;
        }
    }
}
