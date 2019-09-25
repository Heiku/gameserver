package com.ljh.gamedemo.module.equip.asyn.run;

import com.ljh.gamedemo.module.equip.bean.Equip;
import com.ljh.gamedemo.module.equip.dao.RoleEquipDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.DELETE;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * 装备信息数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class EquipEntitySaveRun implements Runnable {

    /**
     * 装备信息
     */
    private Equip equip;

    /**
     * 操作类型
     */
    private int type;

    /**
     * RoleEquipDao
     */
    private RoleEquipDao dao = SpringUtil.getBean(RoleEquipDao.class);

    public EquipEntitySaveRun(Equip equip, int type){
        this.equip = equip;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case UPDATE:
                n = dao.updateRoleEquip(equip);
                log.info("update role_equip, affected row: " + n);
                break;

            case DELETE:
                n = dao.deleteRoleEquip(equip);
                log.info("delete equip, affected rows: " + n);
                break;
        }
    }
}
