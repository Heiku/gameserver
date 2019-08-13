package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.entity.dto.RoleBuff;
import com.ljh.gamedemo.local.cache.RoleBuffCache;
import com.ljh.gamedemo.local.cache.RoleInvitePKCache;
import com.ljh.gamedemo.run.record.FutureMap;
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
     * 持续掉血的技能
     */
    private Spell spell;

    /**
     * 受到技能伤害的玩家
     */
    private Role role;

    /**
     * 技能的总伤害
     */
    private int allDamage;

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

    // 获取userService
    private UserService userService = SpringUtil.getBean(UserService.class);

    private UserBeAttackedRun run = new UserBeAttackedRun();

    public UserBeAttackedScheduleRun(Role role, Spell spell, int extra, boolean pk){
        this.role = role;
        this.spell = spell;
        this.extra = extra;
        this.pk = pk;
        this.allDamage = spell.getDamage();
    }

    @Override
    public void run() {
        int hp = role.getHp();
        int damage = spell.getDamage() / spell.getSec() + extra;

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
                    userService.reliveRole(role);
                }
                return;
            }
        }
        // 更新hp
        role.setHp(hp);
        userService.updateRoleInfo(role);

        // 消息回复
        run.responseAttacked(role);
    }
}
