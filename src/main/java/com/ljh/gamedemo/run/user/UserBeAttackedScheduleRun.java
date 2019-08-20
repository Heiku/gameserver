package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.entity.DurationAttack;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.entity.dto.RoleBuff;
import com.ljh.gamedemo.local.cache.RoleBuffCache;
import com.ljh.gamedemo.local.cache.RoleInvitePKCache;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.service.RoleService;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;

import java.util.List;

/**
 * 玩家持续掉血任务，（中毒）
 *
 * @Author: Heiku
 * @Date: 2019/8/12
 */
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
     * RoleServie
     */
    private RoleService roleService = SpringUtil.getBean(RoleService.class);

    private UserBeAttackedRun run = new UserBeAttackedRun();

    public UserBeAttackedScheduleRun(Role _role, DurationAttack _da, int _extra, boolean _pk){
        this.role = _role;
        this.extra = _extra;
        this.pk = _pk;
        this.da = _da;
    }

    @Override
    public void run() {
        int hp = role.getHp();
        int damage = da.getDamage() / da.getSec() + extra;

        // 掉血先扣盾
        List<RoleBuff> buffList = RoleBuffCache.getCache().getIfPresent(role.getRoleId());
        if (buffList != null && !buffList.isEmpty()){
            hp = run.cutShield(buffList, hp);
        } else {
            // 没有buff，也直接扣血
            hp -= damage;
            sumDamage += damage;

            //玩家死亡，取消任务
            if (hp <= 0){

                // 取消持续掉血任务
                FutureMap.getFutureMap().remove(this.hashCode()).cancel(true);

                // pk 处理
                if (pk){
                    // 移除 pk 记录
                    RoleInvitePKCache.getPkFutureMap().remove(role.getRoleId());
                    run.pkEnd(role);
                }else {
                    roleService.reliveRole(role);
                }
                return;
            }
        }
        // 更新hp
        role.setHp(hp);
        roleService.updateRoleInfo(role);

        // 消息回复
        run.responseAttacked(role);
    }
}
