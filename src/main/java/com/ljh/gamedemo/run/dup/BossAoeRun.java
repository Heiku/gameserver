package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.common.ChallengeType;
import com.ljh.gamedemo.entity.BossSpell;
import com.ljh.gamedemo.entity.Duplicate;
import com.ljh.gamedemo.entity.Partner;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.PartnerCache;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.service.PartnerService;
import com.ljh.gamedemo.util.SpringUtil;

import java.util.Deque;

/**
 * Boss进行 AOE 攻击的任务
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */
public class BossAoeRun implements Runnable {

    /**
     * 副本信息
     */
    private Duplicate dup;

    /**
     * 技能信息
     */
    private BossSpell s;

    /**
     * 获取伙伴服务
     */
    private static PartnerService partnerService;

    static {
        partnerService = SpringUtil.getBean(PartnerService.class);
    }

    public BossAoeRun(Duplicate _dup, BossSpell _s){
        this.dup = _dup;
        this.s = _s;
    }

    @Override
    public void run() {
        // 获取挑战列表
        Deque<Long> deque = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (deque == null || deque.isEmpty()){
            return;
        }

        // 开始制造伤害
        for (Long id : deque) {
            // 玩家受到伤害
            Role role = LocalUserMap.getIdRoleMap().get(id);
            if (role != null){
                UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), s.getDamage(), false);
                UserExecutorManager.addUserTask(role.getUserId(), task);
                continue;
            }

            // partner受到伤害
            Partner partner = PartnerCache.getIdPartnerMap().get(id);
            if (partner != null) {
                partner.setHp(partner.getHp() - s.getDamage());
                partnerService.updatePartner(partner, ChallengeType.DUPLICATE);
            }
        }
    }
}
