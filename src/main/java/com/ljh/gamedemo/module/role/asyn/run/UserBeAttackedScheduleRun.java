package com.ljh.gamedemo.module.role.asyn.run;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.spell.tmp.DurationAttack;
import com.ljh.gamedemo.module.pk.service.PKService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.pk.cache.RoleInvitePKCache;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.module.base.asyn.run.FutureMap;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 玩家持续掉血任务，（中毒）
 *
 * @Author: Heiku
 * @Date: 2019/8/12
 */
@Slf4j
public class UserBeAttackedScheduleRun implements Runnable {

    /**
     * 受到技能伤害的玩家
     */
    private Role role;

    /**
     * 技能伤害参数
     */
    private DurationAttack da;

    /**
     * 技能的累计伤害
     */
    private int sumDamage = 0;

    /**
     * buff影响的额外伤害值
     */
    private int extra;

    /**
     * 是否为pk
     */
    private boolean pk;


    /**
     * 玩家服务
     */
    private RoleService roleService = SpringUtil.getBean(RoleService.class);

    /**
     * pk服务
     */
    private PKService pkService = SpringUtil.getBean(PKService.class);

    /**
     * 协议服务
     */
    private ProtoService protoService = SpringUtil.getBean(ProtoService.class);


    public UserBeAttackedScheduleRun(Role _role, DurationAttack _da, int _extra, boolean _pk){
        this.role = _role;
        this.extra = _extra;
        this.pk = _pk;
        this.da = _da;
    }

    @Override
    public void run() {
        int damage = da.getDamage() / da.getSec() + extra;
        int hp = roleService.cutShield(role, damage);

        // 累计持续掉血量
        sumDamage += damage;

        if (sumDamage >= da.getDamage()){
            FutureMap.getFutureMap().get(this.hashCode()).cancel(true);
            return;
        }

        // 玩家死亡，取消任务
        if (hp <= 0){

            // pk 处理
            if (pk){
                // 移除 pk 记录
                RoleInvitePKCache.getPkFutureMap().remove(role.getRoleId());
                pkService.pkEnd(role);
                return;
            }

            // 玩家被击杀
            roleService.doRoleDeath(role);
            return;
        }

        // 更新hp
        role.setHp(hp);
        roleService.updateRoleInfo(role);

        // 消息回复
        protoService.sendAttackedMsg(role, ContentType.ATTACK_DURATION);
    }
}
