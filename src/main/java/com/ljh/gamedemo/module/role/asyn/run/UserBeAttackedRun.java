package com.ljh.gamedemo.module.role.asyn.run;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.creep.service.AttackCreepService;
import com.ljh.gamedemo.module.group.service.GroupService;
import com.ljh.gamedemo.module.pk.service.PKService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 玩家受到伤害的掉血任务
 *
 * @Author: Heiku
 * @Date: 2019/7/19
 */

@Slf4j
public class UserBeAttackedRun implements Runnable {

    /**
     * 玩家标识id
     */
    private Long userId;

    /**
     * 玩家收到的伤害值
     */
    private Integer damage;

    /**
     * 是否是pk
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



    public UserBeAttackedRun(long userId, Integer damage, boolean pk){
        this.userId = userId;
        this.damage = damage;
        this.pk = pk;
    }


    /**
     * 用户扣血：
     *      优先扣护盾
     *          判断是否有护盾技能
     *          判断护盾是否再有效期内
     *          判断护盾值是否 > 0 ?
     *
     *          扣护盾值，如果护盾碎了，额外扣血
     *      再扣血
     */
    @Override
    public void run() {
        // 获取基础信息
        Role role = LocalUserMap.userRoleMap.get(userId);

        // 掉血优先扣护盾值，判断护盾的值，及护盾是否有效
        int hp = roleService.cutShield(role, damage);

        // 玩家死亡
        if (hp < 0){

            // 如果是pk对决，产生pk结果
            if (pk){
                pkService.pkEnd(role);
                return;
            }

            // 玩家被野怪击杀
            roleService.doRoleDeath(role);
            return;
        }

        // 更新玩家的血量值
        role.setHp(hp);
        roleService.updateRoleInfo(role);

        // 消息通知
        protoService.sendAttackedMsg(role,  ContentType.ATTACK_CURRENT);
    }
}
