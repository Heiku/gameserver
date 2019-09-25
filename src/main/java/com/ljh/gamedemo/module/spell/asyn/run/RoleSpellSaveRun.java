package com.ljh.gamedemo.module.spell.asyn.run;

import com.ljh.gamedemo.module.spell.bean.RoleSpell;
import com.ljh.gamedemo.module.spell.dao.RoleSpellDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;

/**
 * 玩家技能数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class RoleSpellSaveRun implements Runnable {

    /**
     * 玩家技能信息
     */
    private RoleSpell rs;

    /**
     * 操作类型
     */
    private int type;

    /**
     * RoleSpellDao
     */
    private RoleSpellDao dao = SpringUtil.getBean(RoleSpellDao.class);

    public RoleSpellSaveRun(RoleSpell rs, int type) {
        this.rs = rs;
        this.type = type;
    }


    @Override
    public void run() {
        int n;
        switch (type){
            case INSERT:
                n = dao.insertRoleSpell(rs);
                log.info("insert into role_spell, affected rows: " + n);
                break;
        }
    }
}
