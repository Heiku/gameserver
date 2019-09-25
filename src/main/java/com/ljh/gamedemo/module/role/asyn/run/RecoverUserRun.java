package com.ljh.gamedemo.module.role.asyn.run;

import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.module.base.asyn.run.RecoverBuff;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.ljh.gamedemo.common.RecoverType.*;

/**
 * 玩家恢复任务
 *
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Slf4j
public class RecoverUserRun implements Runnable {

    /**
     * 玩家信息
     */
    private Role role;

    /**
     * 额外的恢复 buff
     */
    private RecoverBuff buff;

    /**
     * 玩家服务
     */
    private RoleService roleService = SpringUtil.getBean(RoleService.class);


    public RecoverUserRun(Role role, RecoverBuff buff){
        this.role = role;
        this.buff = buff;
    }


    @Override
    public void run() {
        // 无buff，采用默认的恢复速度
        buff = Optional.ofNullable(buff).orElse(new RecoverBuff(DEFAULT_HP_SEC, DEFAULT_MP_SEC));

        // 玩家的最大生命值
        int maxHp = role.getMaxHp();

        // 判断当前的血量值
        int hp = role.getHp();
        if (hp < maxHp){
            int hpSec = buff.getHpBuf();
            hp += hpSec;
            hp = hp > maxHp ? maxHp : hp;

            // 设置最新的血量值
            role.setHp(hp);
        }

        // 判断当前的蓝量值
        int mp = role.getMp();
        if (mp < MAX_MP){
            int mpSec = buff.getMpBuf();
            mp += mpSec;
            mp = mp > MAX_MP ? maxHp : mp;

            // 设置最新的蓝量值
            role.setHp(mp);
        }

        // 加入玩家线程池中更新属性信息
        roleService.updateRoleInfo(role);
    }
}
