package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.service.DuplicateService;
import com.ljh.gamedemo.service.EquipService;
import com.ljh.gamedemo.service.ProtoService;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
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

    // 通信 channel
    private Channel channel;

    private MsgDuplicateProto.ResponseDuplicate response;

    private ProtoService protoService = ProtoService.getInstance();

    // 引入 UserService
    private UserService userService = SpringUtil.getBean(UserService.class);

    // 引入 EquipService
    private EquipService equipService = SpringUtil.getBean(EquipService.class);

    // 引入 DuplicateService
    private DuplicateService duplicateService = SpringUtil.getBean(DuplicateService.class);

    private static BossBeAttackedRun run = new BossBeAttackedRun();

    public BossBeAttackedRun(){

    }

    public BossBeAttackedRun(Role role, int damage , Boss boss){
        this.role = role;
        this.damage = damage;
        this.boss = boss;
        this.channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
    }

    public static BossBeAttackedRun getInstance(){
        if (run != null){
            return run;
        }
        return new BossBeAttackedRun();
    }

    @Override
    public void run() {
        int hp = boss.getHp();
        if (hp > 0){
            hp -= damage;

            // Boss 死亡
            if (hp <= 0){
                // 取消玩家与Boss的关联任务
                duplicateService.removeAttackedFuture(role);

                // 移除Boss信息
                Duplicate dup = duplicateService.getDuplicate(role);
                dup.getBosses().remove(boss);
                LocalAttackCreepMap.getCurDupMap().put(role.getRoleId(), dup);


                // 重新获取副本中的 Boss 信息
                List<Boss> nowBosses = dup.getBosses();
                boolean empty = nowBosses.isEmpty();

                // 发送通知玩家该 Boss 已经死亡
                sendBossKilledMsg(boss, channel, empty);
                boss = null;

                // 打 Boss的时间判断
                attackTime(empty, role, dup, nowBosses);
                return;

            }

            log.info("Boss: " + boss.getName() + " 的血量为：" + boss.getHp());
            boss.setHp(hp);
            log.info("Boss: " + boss.getName() + " 受到伤害后，血量为：" + boss.getHp());

            if (boss != null) {
                sendBossAttackedMsg(boss, channel);
            }
        }
    }

    /**
     * 为玩家发放奖励，这里是立即得到的物品，不做队列处理
     *
     * @param role
     * @param dup
     */
    private void sendReward(Role role, Duplicate dup, Channel channel) {

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
        userService.updateRoleInfo(role);

        // 更新玩家的装备信息
        equipService.addRoleEquips(role, gList);

        // 同时返回给玩家奖励信息
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.DUPLICATE_CHALLENGE_SUCCESS)
                .setType(MsgDuplicateProto.RequestType.DUPLICATE)
                .addDuplicate(protoService.transToDuplicate(dup))
                .addAllEquip(protoService.transToEquipList(equips))
                .build();

        channel.writeAndFlush(response);
    }

    /**
     * 返回 Boss 死亡的消息
     *
     * @param boss
     * @param channel
     * @param empty
     */
    public void sendBossKilledMsg(Boss boss, Channel channel, boolean empty) {
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

        channel.writeAndFlush(response);
    }


    /**
     * 返回攻击Boss 成功的消息
     *
     * @param boss
     * @param channel
     */
    public void sendBossAttackedMsg(Boss boss, Channel channel){
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgDuplicateProto.RequestType.CHALLENGE)
                .setContent(ContentType.DUPLICATE_ATTACKED_SUCCESS)
                .addBoss(protoService.transToBoss(boss))
                .build();

        channel.writeAndFlush(response);
    }


    /**
     * 返回副本挑战失败的消息
     *
     * @param channel
     * @param content
     */
    private void sendDuplicateFailed(Channel channel, String content){
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        channel.writeAndFlush(response);
    }


    /**
     * 攻击时间判断
     *
     * @param empty
     * @param role
     * @param dup
     * @param nowBosses
     */
    public Boss attackTime(boolean empty, Role role, Duplicate dup, List<Boss> nowBosses){
        // 如果该副本中已经不存在 Boss 信息，那么开始为队伍发放奖励
        // 同时清空副本信息
        if (empty){
            // 通过时间的判断
            long now = System.currentTimeMillis();
            long last = LocalAttackCreepMap.getDupTimeStampMap().get(role.getRoleId());
            long duration = dup.getLimitTime() * 1000;
            if (now - last <= duration){

                // 副本挑战成功
                sendReward(role, dup, channel);
            }else {
                // 超过时间，挑战失败
                sendDuplicateFailed(channel, ContentType.DUPLICATE_TIME_OUT);
            }

            // 副本资源释放，等待回收
            LocalAttackCreepMap.getDupTimeStampMap().remove(role.getRoleId());
            LocalAttackCreepMap.getCurDupMap().remove(role.getRoleId());
            dup = null;

            return null;
        }else {
            // 如果存在第二个Boss的话，那么 Second Boss 会重新根据队伍中的角色进行血量扣除
            boss = nowBosses.get(0);
            duplicateService.doBossAttacked(dup);

            return boss;
        }
    }
}
