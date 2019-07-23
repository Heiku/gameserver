package com.ljh.gamedemo.run.creep;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.local.LocalCreepMap;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.util.CountDownLatchUtil;
import com.ljh.gamedemo.service.ProtoService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;


/**
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Slf4j
public class CreepBeAttackedScheduleRun implements Runnable {

    private Spell spell;

    private Integer creepId;

    private Channel channel;

    private boolean useLatch;

    private Integer allDamage;

    private Integer sumDamage = 0;

    private ProtoService protoService = ProtoService.getInstance();

    private boolean firstLatch = true;

    public CreepBeAttackedScheduleRun(Spell spell, Integer creepId,
                                      Channel channel,boolean useLatch){
        this.spell = spell;
        this.creepId = creepId;
        this.channel = channel;
        this.useLatch = useLatch;

        // 持续伤害的总伤害值
        this.allDamage = spell.getDamage();

    }

    @Override
    public void run() {
        // 判断是否使用了CountDownLatch
        if (firstLatch) {
            if (useLatch){
                try {
                    firstLatch = false;
                    CountDownLatchUtil.await();
                } catch (RejectedExecutionException e) {
                    throw e;
                }
            }
        }

        Creep creep = LocalCreepMap.getIdCreepMap().get(creepId);
        int hp = creep.getHp();
        int startUp = creep.getMaxHp();
        // 每秒造成的伤害值
        int damage = spell.getDamage() / spell.getSec();

        log.info("野怪持续掉血任务，野怪掉血前：" + creep.getHp());
        if (hp > 0){
            hp -= damage;
            sumDamage += damage;

            if (hp <= 0){
                creep.setHp(startUp);
                creep.setNum(creep.getNum() - 1);
                // 更新cache
                LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

                // 同时，野怪死亡，取消任务
                FutureMap.futureMap.get(this.hashCode()).cancel(true);
                log.info("野怪持续掉血任务：野怪死亡，任务取消");

                MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                        .setType(MsgAttackCreepProto.RequestType.ATTACK)
                        .setResult(ResultCode.SUCCESS)
                        .setContent(ContentType.ATTACK_DEATH_CREEP)
                        .setCreep(protoService.transToCreep(creep))
                        .build();
                channel.writeAndFlush(response);
                return;
            }

            // 判断掉血值
            if (sumDamage >= allDamage){
                FutureMap.futureMap.get(this.hashCode()).cancel(true);
                log.info("野怪持续掉血任务：达到最大掉血值，任务取消");
                return;
            }

            // 扣血
            creep.setHp(hp);
            LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

            log.info("野怪持续掉血任务，野怪掉血后：" + creep.getHp());

            // 最后攻击成功，返回消息给client
            MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setType(MsgAttackCreepProto.RequestType.ATTACK)
                    .setResult(ResultCode.SUCCESS)
                    .setContent(ContentType.ATTACK_CURRENT)
                    .setCreep(protoService.transToCreep(creep))
                    .build();
            channel.writeAndFlush(response);
        }
    }
}
