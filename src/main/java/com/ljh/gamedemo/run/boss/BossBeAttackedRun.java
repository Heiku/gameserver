package com.ljh.gamedemo.run.boss;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.cache.RoleAttrCache;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.service.DuplicateService;
import com.ljh.gamedemo.service.EquipService;
import com.ljh.gamedemo.service.ProtoService;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/30
 */

@Slf4j
public class BossBeAttackedRun implements Runnable {

    // 获取玩家信息
    private Role role;

    // 获取玩家技能
    private Spell spell;

    // 获取 Boss 信息
    private Boss boss;

    // 标识额外伤害
    private int extra;

    // 标识是否是普攻攻击
    private boolean attack;

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

    public BossBeAttackedRun(Role role, Spell spell, Boss boss, boolean attack, Channel channel){
        this.role = role;
        this.spell = spell;
        this.boss = boss;
        this.attack = attack;
        this.channel = channel;
    }

    public static BossBeAttackedRun getInstance(){
        if (run != null){
            return run;
        }
        return new BossBeAttackedRun();
    }

    @Override
    public void run() {

        // 获取攻击的方式
        if (attack){
            extra = RoleAttrCache.getRoleAttrMap().get(role.getRoleId()).getDamage();
        }else {
            extra = RoleAttrCache.getRoleAttrMap().get(role.getRoleId()).getSp();
        }

        // 获取 Boss 的血量
        int hp = boss.getHp();
        // 获取玩家的伤害值
        int damage = spell.getDamage() + extra;

        if (hp > 0){
            hp -= damage;

            // Boss 死亡
            if (hp <= 0){

                // 将 Boss移除副本信息
                Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());
                dup.getBosses().remove(boss);
                LocalAttackCreepMap.getCurDupMap().put(role.getRoleId(), dup);

                // Boss GG，消除 Boss的自动攻击任务
                // 组队的话，那么一个副本应该对应多个玩家 duplicate, List<RoleId>
                // 所以，取消任务的话，应该是每个玩家都取消掉血任务
                LocalAttackCreepMap.getUserBeAttackedMap().get(role.getRoleId()).cancel(true);
                LocalAttackCreepMap.getUserBeAttackedMap().remove(role.getRoleId());

                // 重新获取副本中的 Boss 信息
                List<Boss> nowBosses = dup.getBosses();
                boolean empty;
                empty = nowBosses.isEmpty();

                // 发送通知玩家该 Boss 已经死亡
                sendBossKilledMsg(boss, channel, empty);
                boss = null;

                // 打 Boss的时间判断
                Boss next = attackTime(empty, role, dup, nowBosses);
                return;
                /*if (next == null){
                    return;
                }*/
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
        int gold = dup.getGoldReward();
        List<Equip> equips = dup.getEquipReward();

        // 更新玩家的金币信息
        role.setGold(role.getGold() + gold);
        userService.updateRoleInfo(role);

        // 更新玩家的装备信息
        equipService.addRoleEquips(role, equips);

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
            duplicateService.userBeAttackedByBoss(dup, channel);

            return boss;
        }
    }
}
