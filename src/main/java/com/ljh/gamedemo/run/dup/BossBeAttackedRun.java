package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.duplicate.bean.Boss;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.duplicate.service.DuplicateService;
import com.ljh.gamedemo.module.duplicate.service.RewardService;
import com.ljh.gamedemo.module.group.bean.Group;
import com.ljh.gamedemo.module.group.service.GroupService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.creep.local.LocalAttackCreepMap;
import com.ljh.gamedemo.module.group.cache.GroupCache;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * 野怪受到直接伤害的task
 *
 * @Author: Heiku
 * @Date: 2019/7/30
 */

@Slf4j
public class BossBeAttackedRun implements Runnable {

    /**
     * 玩家信息
     */
    private Role role;

    /**
     * Boss 信息
     */
    private Boss boss;

    /**
     * Boos受到的伤害值
     */
    private int damage;

    /**
     * 是否为队伍
     */
    private boolean isGroup;

    /**
     * 副本挑战id
     */
    private Long bindId;

    /**
     * channel
     */
    private Channel channel;

    /**
     * ChannelGroup
     */
    private ChannelGroup cg;

    /**
     * 协议返回
     */
    private MsgDuplicateProto.ResponseDuplicate response;

    /**
     * 副本服务
     */
    private DuplicateService duplicateService = SpringUtil.getBean(DuplicateService.class);

    /**
     * 组队服务
     */
    private GroupService groupService = SpringUtil.getBean(GroupService.class);

    /**
     * 奖励服务
     */
    private RewardService rewardService = SpringUtil.getBean(RewardService.class);

    /**
     * 协议服务
     */
    private ProtoService protoService = ProtoService.getInstance();

    public BossBeAttackedRun(Role role, int damage , Boss boss){
        this.role = role;
        this.damage = damage;
        this.boss = boss;

        // 判断是否组队
        isGroup = groupService.hasGroup(role);
        // 获取挑战id
        bindId = duplicateService.getBindId(role);

        // 初始化协议通道
        channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
        if (isGroup){
            cg = ChannelCache.getGroupChannelMap().get(bindId);
        }
    }

    @Override
    public void run() {
        int hp = boss.getHp();
        if (hp > 0){
            hp -= damage;

            // Boss 死亡
            if (hp <= 0){
                doBossDeath();
                return;

            }
            boss.setHp(hp);
            if (boss != null) {
                sendBossAttackedMsg(boss);
            }
        }
    }


    /**
     * Boss死亡的具体操作
     */
    public void doBossDeath(){
        // 获取当前的副本信息
        Duplicate dup = duplicateService.getDuplicate(role);
        dup.getBosses().remove(boss);
        boolean empty = dup.getBosses().isEmpty();

        // 发送通知玩家该 Boss 已经死亡
        sendBossKilledMsg(boss, empty);

        // 取消当前场景中的受到攻击的任务
        duplicateService.removeDupFutureList(dup);

        // 判断是否通关
        attackTime(empty, role, dup);
    }





    /**
     * 攻击时间判断
     *
     * @param empty
     * @param role
     * @param dup
     */
    public void attackTime(boolean empty, Role role, Duplicate dup){
        long bindId = duplicateService.getBindId(role);
        // 如果该副本中已经不存在 Boss 信息，那么开始为队伍发放奖励
        // 同时清空副本信息
        if (empty){
            // 通过时间的判断
            long now = System.currentTimeMillis();
            long last = LocalAttackCreepMap.getDupTimeStampMap().get(bindId);
            long duration = dup.getLimitTime() * 1000;

            if (now - last <= duration){
                // 副本挑战成功
                if (!isGroup) {
                    rewardService.sendRoleReward(role, dup);
                }else {
                    Group group = GroupCache.getGroupCache().getIfPresent(bindId);
                    rewardService.sendGroupReward(group, dup);
                }
            }else {
                // 超过时间，挑战失败
                if (!isGroup) {
                    duplicateService.sendDuplicateFailed(channel, ContentType.DUPLICATE_TIME_OUT);
                }else {
                    duplicateService.sendDuplicateFailed(cg, ContentType.DUPLICATE_TIME_OUT);
                }
            }

            // 副本销毁
            duplicateService.destroyDupSource(null, dup);
        }else {
            // 如果存在第二个Boss的话，那么 Second Boss 会重新根据队伍中的角色进行血量扣除
            boss = dup.getBosses().get(0);
            duplicateService.doBossAttacked(dup);
        }
    }


    /**
     * 返回攻击Boss 成功的消息
     *
     * @param boss  Boss信息
     */
    public void sendBossAttackedMsg(Boss boss){
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgDuplicateProto.RequestType.CHALLENGE)
                .setContent(ContentType.DUPLICATE_ATTACKED_SUCCESS)
                .addBoss(protoService.transToBoss(boss))
                .build();
        channel.writeAndFlush(response);
    }



    /**
     * 返回 Boss 死亡的消息
     *
     * @param boss      Boss信息
     * @param empty     是否存在Boss
     */
    public void sendBossKilledMsg(Boss boss, boolean empty) {
        String content;

        // 判断是否还有其他 Boss
        if (empty){
            content = ContentType.DUPLICATE_BOSS_NOW_DEATH;
        }else {
            content = ContentType.DUPLICATE_BOSS_NEXT;
        }
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgDuplicateProto.RequestType.CHALLENGE)
                .setContent(content)
                .addBoss(protoService.transToBoss(boss))
                .build();

        if (isGroup){
            cg.writeAndFlush(response);
        }else {
            channel.writeAndFlush(response);
        }
    }
}
