package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.entity.BossSpell;
import com.ljh.gamedemo.entity.Duplicate;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.tmp.RoleDeBuff;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.RoleBuffCache;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.user.UserDizzinessRun;

import java.util.Deque;

/**
 * Boss 进行眩晕攻击
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */
public class BossDizzinessRun implements Runnable {

    /**
     * 副本信息
     */
    private Duplicate dup;

    /**
     * 技能信息
     */
    private BossSpell s;

    public BossDizzinessRun(Duplicate _dup, BossSpell _s){
        this.dup = _dup;
        this.s = _s;
    }

    @Override
    public void run() {
        // 获取挑战者列表
        Deque<Long> deque = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (deque == null || deque.isEmpty()){
            return;
        }

        // 开始群体眩晕 （玩家将被眩晕， 伙伴免疫控制）
        for (Long id : deque) {
            // 玩家受到伤害
            Role role = LocalUserMap.getIdRoleMap().get(id);
            if (role != null){
                // 玩家停止动作
                UserDizzinessRun task = new UserDizzinessRun(s.getSec());
                UserExecutorManager.addUserTask(role.getUserId(), task);

                // 设置眩晕的缓存信息
                // 设置deBuff信息
                RoleDeBuff deBuff = new RoleDeBuff();
                deBuff.setSec(s.getSec());
                deBuff.setTs(System.currentTimeMillis());
                RoleBuffCache.getRoleDeBuffMap().put(role.getRoleId(), deBuff);
            }
        }
    }
}
