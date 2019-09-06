package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.common.ChallengeType;
import com.ljh.gamedemo.module.duplicate.bean.BossSpell;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.spell.bean.Partner;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.spell.cache.PartnerCache;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.module.duplicate.service.DuplicateService;
import com.ljh.gamedemo.module.spell.service.PartnerService;
import com.ljh.gamedemo.util.SpringUtil;

/**
 * Boss选取目标并进行普通攻击
 *
 * @Author: Heiku
 * @Date: 2019/8/16
 */
public class BossDoAttackedRun implements Runnable {

    /**
     * 副本信息
     */
    private Duplicate dup;

    /**
     * 技能信息
     */
    private BossSpell s;

    /**
     * partnerService
     */
    private PartnerService partnerService = SpringUtil.getBean(PartnerService.class);

    /**
     * duplicateService
     */
    private DuplicateService dupService = SpringUtil.getBean(DuplicateService.class);

    public BossDoAttackedRun(Duplicate _dup, BossSpell _s){
        this.dup = _dup;
        this.s = _s;
    }

    @Override
    public void run() {
        // 获取目标的id
        long targetId = dupService.getFirstAimFromQueue(dup);
        if (targetId == 0){
            dupService.destroyDupSource(null, dup);
        }

        // 目标为玩家
        Role role = LocalUserMap.getIdRoleMap().get(targetId);
        if (role != null) {
            UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), s.getDamage(), false);
            UserExecutorManager.addUserTask(role.getUserId(), task);
            return;
        }

        // 目标为玩家伙伴
        Partner partner = PartnerCache.getIdPartnerMap().get(targetId);
        if (partner != null){
            // 更新伙伴的信息
            partner.setHp(partner.getHp() - s.getDamage());
            partnerService.updatePartner(partner, ChallengeType.DUPLICATE);
        }
    }
}
