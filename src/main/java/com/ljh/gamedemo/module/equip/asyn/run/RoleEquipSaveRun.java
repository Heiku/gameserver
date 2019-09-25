package com.ljh.gamedemo.module.equip.asyn.run;

import com.ljh.gamedemo.module.equip.bean.RoleEquip;
import com.ljh.gamedemo.module.equip.dao.RoleEquipDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;

/**
 * 玩家装备数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class RoleEquipSaveRun implements Runnable {


    /**
     * 玩家装备信息
     */
    private RoleEquip roleEquip;


    /**
     * 操作类型
     */
    private int type;


    /**
     * RoleEquipDao
     */
    private RoleEquipDao dao = SpringUtil.getBean(RoleEquipDao.class);


    public RoleEquipSaveRun(RoleEquip re, int type){
        this.roleEquip = re;
        this.type = type;
    }



    @Override
    public void run() {
        switch (type){
            case INSERT:
                int n = dao.insertRoleEquip(roleEquip);
                log.info("insert role_equip, affected row: " + n);
                break;
        }
    }
}
