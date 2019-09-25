package com.ljh.gamedemo.module.role.asyn.run;

import com.ljh.gamedemo.module.role.bean.RoleAttr;
import com.ljh.gamedemo.module.role.dao.RoleAttrDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * 玩家属性数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */
@Slf4j
public class RoleAttrSaveRun implements Runnable{

    /**
     * 玩家属性信息
     */
    private RoleAttr attr;

    /**
     * 操作类型
     */
    private int type;

    /**
     * RoleAttrDao
     */
    private RoleAttrDao dao = SpringUtil.getBean(RoleAttrDao.class);

    public RoleAttrSaveRun(RoleAttr attr, int type) {
        this.attr = attr;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertRoleAttr(attr);
                log.info("insert role_attr, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateRoleAttr(attr);
                log.info("update role_attr, affected rows: " + n);
                break;
        }
    }
}
