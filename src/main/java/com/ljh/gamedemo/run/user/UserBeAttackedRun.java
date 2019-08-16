package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.dto.RoleBuff;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.RoleBuffCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.service.GroupService;
import com.ljh.gamedemo.service.PKService;
import com.ljh.gamedemo.service.ProtoService;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 玩家受到伤害的掉血任务
 *
 * @Author: Heiku
 * @Date: 2019/7/19
 */

@Slf4j
public class UserBeAttackedRun implements Runnable {

    // 这里只需要一个userId，在去map中读取最新的role
    private Long userId;

    // 玩家收到的伤害值
    private Integer damage;

    // 用于通知掉血
    private Channel channel;

    // 是否是pk
    private boolean pk;

    // base userService
    private UserService userService = SpringUtil.getBean(UserService.class);

    // pkService
    private PKService pkService = SpringUtil.getBean(PKService.class);

    // groupService
    private GroupService groupService = SpringUtil.getBean(GroupService.class);

    // protoService Util
    private ProtoService protoService = ProtoService.getInstance();


    public UserBeAttackedRun(){

    }

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

        int hp = role.getHp();
        log.info("攻击前的 role 属性为："  + role);

        // 掉血优先扣护盾值，判断护盾的值，及护盾是否有效
        List<RoleBuff> buffList = RoleBuffCache.getCache().getIfPresent(role.getRoleId());
        if (buffList != null && !buffList.isEmpty()){
            hp = cutShield(buffList, hp);
        } else {
            // 没有buff，也直接扣血
            hp -= damage;
        }
        if (hp < 0){
            // 退出队伍
            groupService.removeGroup(role);
            // 玩家复活
            userService.reliveRole(role);
            return;
        }
        role.setHp(hp);

        // 更新map
        userService.updateRoleInfo(role);

        // 最终消息返回
        responseAttacked(role);

        // pk 对决
        // 判断玩家的hp，生成pk 结果
        if (pk){
            if (role.getHp() <= 0) {
                pkEnd(role);
            }
        }
    }


    /**
     * 扣除护盾值
     *
     * @param buffList
     * @param hp
     * @return
     */
    public int cutShield(List<RoleBuff> buffList, int hp){
        // 记录护盾打碎后多余的扣血值
        int blood;

        // 获取护盾的buff
        RoleBuff buff = new RoleBuff();
        for (RoleBuff b : buffList) {
            if (b.getType() == 1){
                buff = b;
            }
        }

        // 获取当前时间点
        long nowTs = System.currentTimeMillis();
        long td = nowTs - buff.getCreateTime();
        long cd = buff.getSec() * 1000;

        // 护盾时间的有效期判断
        if (td < cd){
            int shield = buff.getShield();
            log.info("存在护盾buff，护盾值为：" + shield);

            // 护盾值有效
            if (shield >= 0) {
                shield -= damage;
                log.info("护盾收到伤害：当前护盾值为: " + shield);

                // 盾碎了
                if (shield <= 0) {
                    // 获取扣血值
                    blood = Math.abs(shield - damage);
                    log.info("护盾碎了，将收到额外的伤害为：" + blood);

                    // 移除 buff
                    buffList.remove(buff);
                    log.info("移除护盾buff后，当前的玩家buff有：" + buffList);

                    // 同时玩家扣血
                    if (hp > 0) {
                        hp -= blood;
                    }
                } else {
                    // 更新shield
                    buff.setShield(shield);
                }
            }
        }else {
            // 护盾技能过期，移除护盾技能
            hp -= damage;
            buffList.remove(buff);
        }

        return hp;
    }


    /**
     * 消息返回
     *
     * @param role
     */
    public void responseAttacked(Role role){
        channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep
                .newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgAttackCreepProto.RequestType.ATTACK)
                .setContent(ContentType.ATTACK_CURRENT)
                .setRole(protoService.transToRole(role))
                .build();
        channel.writeAndFlush(response);
    }

    /**
     * pk 结束
     */
    public void pkEnd(Role role){
        pkService.generatePkRecord(role);
        userService.reliveRole(role);
    }
}
