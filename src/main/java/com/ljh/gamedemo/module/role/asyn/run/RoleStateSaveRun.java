package com.ljh.gamedemo.module.role.asyn.run;

import com.ljh.gamedemo.module.role.bean.RoleState;
import com.ljh.gamedemo.module.role.dao.RoleStateDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * 玩家在线状态数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class RoleStateSaveRun implements Runnable {

    /**
     * 玩家状态信息
     */
    private RoleState state;

    /**
     * 操作类型
     */
    private int type;

    /**
     * RoleStateDao
     */
    private RoleStateDao dao = SpringUtil.getBean(RoleStateDao.class);


    public RoleStateSaveRun(RoleState state, int type) {
        this.state = state;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertUserState(state);
                log.info("insert into role_state, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateUserState(state);
                log.info("update role_state, affected rows: " + n);
                break;
        }
    }
}
