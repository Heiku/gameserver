package com.ljh.gamedemo.run.creep;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.local.LocalCreepMap;
import com.ljh.gamedemo.local.cache.RoleAttrCache;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.proto.protoc.RoleProto;
import com.ljh.gamedemo.run.util.CountDownLatchUtil;
import com.ljh.gamedemo.service.ProtoService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * @Author: Heiku
 * @Date: 2019/7/19
 */

@Slf4j
public class CreepBeAttackedRun implements Runnable {

    private Spell spell;

    private Integer creepId;

    private Channel channel;

    private Integer siteId;

    private ProtoService protoService = ProtoService.getInstance();

    private boolean useLatch;

    private Integer extra;

    private boolean attack;

    private Role role;

    public CreepBeAttackedRun(Role role, Spell spell, Integer creepId, Channel channel, boolean attack, boolean useLatch){
        this.spell = spell;
        this.creepId = creepId;
        this.channel = channel;
        this.useLatch = useLatch;
        this.siteId = role.getSiteId();
        this.attack = attack;
        this.role = role;
    }

    @Override
    public void run() {
        if (useLatch){
            try {
                CountDownLatchUtil.await();
            } catch (RejectedExecutionException e) {
                throw e;
            }
        }

        // 判断攻击的类型，决定buff的增益效果
        if (attack){
            extra = RoleAttrCache.getRoleAttrMap().get(role.getRoleId()).getDamage();
        }else {
            extra = RoleAttrCache.getRoleAttrMap().get(role.getRoleId()).getSp();
        }

        // 指定攻击的野怪
        Creep creep = LocalCreepMap.getIdCreepMap().get(creepId);
        int startHp = creep.getHp();

        int hp = creep.getHp();
        int damage = spell.getDamage() + extra;

        if (hp > 0){
            hp -= damage;

            // 野怪死亡
            if (hp <= 0){
                creep.setHp(startHp);
                creep.setNum(creep.getNum() - 1);
                LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

                MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                        .setType(MsgAttackCreepProto.RequestType.ATTACK)
                        .setResult(ResultCode.SUCCESS)
                        .setContent(ContentType.ATTACK_DEATH_CREEP)
                        .setCreep(protoService.transToCreep(creep))
                        .build();
                channel.writeAndFlush(response);
            }

            log.info("野怪收到伤害前属性为：" + creep);
            // 正常扣血
            creep.setHp(hp);
            LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

            log.info("野怪受到伤害后，属性为：" + creep);

            Creep c1 = LocalCreepMap.getNameCreepMap().get(creep.getName());
            log.info("nameCreepMap更新后，creep属性为：" + c1);

            List<Creep> creepList = LocalCreepMap.getSiteCreepMap().get(creep.getSiteId());
            for (Creep c : creepList) {
                if (c.getCreepId().intValue() == creepId)
                    log.info("siteCreepMap更新后,creep属性为：" + c);
            }

            // 最后攻击成功，返回消息给client
            MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setType(MsgAttackCreepProto.RequestType.ATTACK)
                    .setResult(ResultCode.SUCCESS)
                    .setContent(ContentType.ATTACK_CURRENT)
                    .setRole(RoleProto.Role.getDefaultInstance())
                    .setCreep(protoService.transToCreep(creep))
                    .build();
            channel.writeAndFlush(response);
        }
    }
}
