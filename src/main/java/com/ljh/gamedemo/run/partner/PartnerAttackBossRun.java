package com.ljh.gamedemo.run.partner;

import com.ljh.gamedemo.entity.Boss;
import com.ljh.gamedemo.entity.Duplicate;
import com.ljh.gamedemo.entity.Partner;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.PartnerCache;
import com.ljh.gamedemo.run.DuplicateManager;
import com.ljh.gamedemo.run.dup.BossBeAttackedRun;

/**
 * 伙伴协助攻击Boss
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */
public class PartnerAttackBossRun implements Runnable {

    /**
     * 副本信息
     */
    private Duplicate dup;

    /**
     * 伙伴信息
     */
    private Partner partner;


    public PartnerAttackBossRun(Duplicate _dup, Partner _partner){
        this.dup = _dup;
        this.partner = _partner;
    }

    @Override
    public void run() {
        // 获取Boss 信息
        if (dup == null || dup.getBosses() == null || dup.getBosses().isEmpty()){
            // 获取不到任务，将future 取消
            PartnerCache.getPartnerFutureMap().get(partner.getId()).cancel(true);
            return;
        }
        Boss b = dup.getBosses().get(0);

        // 执行普攻掉血任务
        Role role = LocalUserMap.getIdRoleMap().get(partner.getRoleId());
        BossBeAttackedRun task = new BossBeAttackedRun(role, partner.getDamage(), b);
        DuplicateManager.addDupTask(dup.getRelatedId(), task);
    }
}
