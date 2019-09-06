package com.ljh.gamedemo.module.spell.service;

import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.spell.bean.Partner;
import com.ljh.gamedemo.module.creep.local.LocalAttackCreepMap;
import com.ljh.gamedemo.module.creep.local.LocalCreepMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.spell.cache.PartnerCache;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.DuplicateManager;
import com.ljh.gamedemo.run.partner.PartnerAttackBossRun;
import com.ljh.gamedemo.module.duplicate.service.DuplicateService;
import io.netty.util.concurrent.ScheduledFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.concurrent.TimeUnit;

import static com.ljh.gamedemo.common.ChallengeType.CREEP;
import static com.ljh.gamedemo.common.ChallengeType.DUPLICATE;

/**
 * 召唤伙伴的相关操作
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */
@Service
public class PartnerService {

    /**
     * DupService
     */
    @Autowired
    private DuplicateService duplicateService;


    /**
     * 更新partner 的属性数据
     *
     * @param partner   玩家
     * @param type      挑战场景类型
     */
    public void updatePartner(Partner partner, int type){
        // 副本挑战中，将伙伴移除副本
        long roleId = partner.getRoleId();
        long bindId = duplicateService.getBindId(LocalUserMap.getIdRoleMap().get(roleId));
        Deque<Long> deque;

        // 如果伙伴已经阵亡
        if (partner.getHp() <= 0){
            // 判断挑战场景
            switch (type){
                case DUPLICATE:
                    deque = LocalAttackCreepMap.getBossAttackQueueMap().get(bindId);
                    deque.remove(partner.getId());
                    break;

                case CREEP:
                    Creep creep = LocalCreepMap.getIdCreepMap().get(LocalAttackCreepMap.getCurrentCreepMap().get(roleId));
                    deque = LocalAttackCreepMap.getCreepAttackedMap().get(creep.getCreepId());
                    deque.remove(partner.getId());
                    break;
            }
            // 同时移除本地的伙伴数据
            removeLocalPartner(partner);
            return;
        }

        // 直接更新Partner
        PartnerCache.getIdPartnerMap().put(partner.getId(), partner);
        PartnerCache.getRolePartnerMap().put(partner.getRoleId(), partner);
    }


    /**
     * 将伙伴加入到副本挑战队列中
     *
     * @param dup       副本信息
     * @param partner   伙伴信息
     */
    public void addPartnerToDup(Duplicate dup, Partner partner){
        Deque<Long> deque = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (deque == null){
            return;
        }
        deque.addFirst(partner.getId());
        LocalAttackCreepMap.getBossAttackQueueMap().put(dup.getRelatedId(), deque);
    }



    /**
     * 移除本地的伙伴数据
     *
     * @param partner   伙伴信息
     */
    private void removeLocalPartner(Partner partner){
        // 缓存删除
        PartnerCache.getRolePartnerMap().remove(partner.getRoleId());
        PartnerCache.getIdPartnerMap().remove(partner.getId());
    }


    /**
     * 伙伴开始攻击 Boss
     *
     * @param p         伙伴信息
     * @param dup       副本信息
     */
    public void attackBoss(Partner p, Duplicate dup) {
        // 构造攻击任务
        PartnerAttackBossRun task = new PartnerAttackBossRun(dup, p);

        // 执行保存记录
        CustomExecutor executor = DuplicateManager.getExecutor(dup.getRelatedId());
        ScheduledFuture future = executor.scheduleAtFixedRate(task, 0, 3, TimeUnit.SECONDS);
        PartnerCache.getPartnerFutureMap().put(p.getId(), future);
    }

}
