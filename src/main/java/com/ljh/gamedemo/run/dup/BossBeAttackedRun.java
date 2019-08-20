package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.GroupCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.service.*;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 野怪受到直接伤害的task
 *
 * @Author: Heiku
 * @Date: 2019/7/30
 */

@Slf4j
public class BossBeAttackedRun implements Runnable {

    // 获取玩家信息
    private Role role;

    // 获取 Boss 信息
    private Boss boss;

    // Boos受到的伤害值
    private int damage;


    // 是否为队伍
    private boolean isGroup;

    // 具体id
    private Long bindId;


    // channel
    private Channel channel;

    // ChannelGroup
    private ChannelGroup cg;


    private MsgDuplicateProto.ResponseDuplicate response;

    private ProtoService protoService = ProtoService.getInstance();

    // RoleService
    private RoleService roleService = SpringUtil.getBean(RoleService.class);

    // EquipService
    private EquipService equipService = SpringUtil.getBean(EquipService.class);

    // DuplicateService
    private DuplicateService duplicateService = SpringUtil.getBean(DuplicateService.class);

    // GroupService
    private GroupService groupService = SpringUtil.getBean(GroupService.class);

    public BossBeAttackedRun(Role role, int damage , Boss boss){
        this.role = role;
        this.damage = damage;
        this.boss = boss;

        isGroup = groupService.hasGroup(role);
        bindId = duplicateService.getBindId(role);

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

        // 回收
        boss = null;
    }

    /**
     * 为玩家发放奖励，这里是立即得到的物品，不做队列处理
     *
     * @param role
     * @param dup
     */
    private void sendRoleReward(Role role, Duplicate dup) {

        // 奖励副本通关信息
        int gold = dup.getGoldReward();
        List<Equip> equips = dup.getEquipReward();

        // 构造物品列表
        List<Goods> gList = new ArrayList<>();
        equips.forEach( e -> {
            Goods goods = new Goods();
            goods.setGid(e.getEquipId());
            gList.add(goods);
        });


        // 更新玩家的金币信息
        role.setGold(role.getGold() + gold);
        roleService.updateRoleInfo(role);

        // 更新玩家的装备信息
        equipService.addRoleEquips(role, gList);

        // 同时返回给玩家奖励信息
        response = combineDupMsg(dup, equips);
        channel.writeAndFlush(response);
    }


    /**
     * 组队发放奖励
     *
     * @param group
     * @param dup
     */
    private void sendGroupReward(Group group, Duplicate dup){
        // 获取奖励信息
        int eachGold = dup.getGoldReward() / group.getMembers().size();
        List<Equip> equips = dup.getEquipReward();

        // 为队伍成员发放奖励
        for (Long m : group.getMembers()) {
            Role r = LocalUserMap.getIdRoleMap().get(m);
            r.setGold(r.getGold() + eachGold);

            List<Goods> goods = new ArrayList<>();
            List<Equip> eList = new ArrayList<>();
            if (!equips.isEmpty()) {
                goods.add(new Goods(equips.get(0).getEquipId(), CommodityType.EQUIP.getCode()));
                eList.add(equips.get(0));
                equips.remove(0);
            }

            // 进行更新
            roleService.updateRoleInfo(r);
            if (!goods.isEmpty()) {
                equipService.addRoleEquips(r, goods);
            }

            // 消息回复
            channel = ChannelCache.getUserIdChannelMap().get(r.getUserId());
            dup.setGoldReward(eachGold);
            response = combineDupMsg(dup, eList);
            channel.writeAndFlush(response);
        }
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
                    sendRoleReward(role, dup);
                }else {
                    Group group = GroupCache.getGroupCache().getIfPresent(bindId);
                    sendGroupReward(group, dup);
                }
            }else {
                // 超过时间，挑战失败
                if (!isGroup) {
                    sendDuplicateFailed(channel, ContentType.DUPLICATE_TIME_OUT);
                }else {
                    sendDuplicateFailed(cg, ContentType.DUPLICATE_TIME_OUT);
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
     * 返回副本挑战失败的消息
     *
     * @param channel   channel
     * @param content   消息
     */
    private void sendDuplicateFailed(Channel channel, String content){
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        channel.writeAndFlush(response);
    }


    /**
     * 返回副本挑战失败的队伍消息
     *
     * @param cg        channelGroup
     * @param content   消息
     */
    private void sendDuplicateFailed(ChannelGroup cg, String content){
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        cg.writeAndFlush(response);
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


    /**
     * 构造奖励回复信息
     *
     * @param dup       副本信息
     * @param equips    装备信息
     * @return
     */
    private MsgDuplicateProto.ResponseDuplicate combineDupMsg(Duplicate dup, List<Equip> equips){
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.DUPLICATE_CHALLENGE_SUCCESS)
                .setType(MsgDuplicateProto.RequestType.DUPLICATE)
                .addDuplicate(protoService.transToDuplicate(dup))
                .addAllEquip(protoService.transToEquipList(equips))
                .build();
        return response;
    }
}
