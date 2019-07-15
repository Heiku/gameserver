package com.ljh.gamedemo.run;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.service.DeathCreepService;
import com.ljh.gamedemo.service.ProtoService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author: Heiku
 * @Date: 2019/7/15
 */

@Slf4j
public class NormalAttackRun implements Runnable {

    private volatile Creep creep;
    private Spell spell;
    private Channel channel;

    private Boolean auto;

    private Integer startHp;

    private DeathCreepService deathCreepService = new DeathCreepService();

    private ProtoService protoService = new ProtoService();

    public NormalAttackRun(Creep creep, Spell spell, Channel channel, boolean auto) {
        this.creep = creep;
        this.spell = spell;
        this.channel = channel;
        this.auto = auto;

        startHp = creep.getHp();
    }

    // 这里暂时只杀一只野怪，对的
    @Override
    public void run() {
        while (true) {
            Creep creep = LocalAttackCreepMap.getChannelCreepMap().get(channel);

            // 获取野怪的血量，技能的伤害值
            int hp = creep.getHp();
            int damage = spell.getDamage();

            // 野怪的生命值 -= 技能的伤害值
            if (creep.getHp() > 0) {
                hp -= damage;
            }
            creep.setHp(hp);
            LocalAttackCreepMap.getChannelCreepMap().put(channel, creep);

            // 服务端调试野怪生命值
            log.info("attack current creep info: " + creep.getHp());

            // 调用deathCreep() 野怪死亡相关
            if (hp < 0) {
                deathCreepService.deathCreep(channel, startHp);
                break;
            }

            // 构建返回文本
            String content;
            if (auto) {
                content = ContentType.ATTACK_CURRENT;
            } else {
                content = ContentType.ATTACK_SPELL_SUCCESS;
            }

            MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep
                    .newBuilder()
                    .setResult(ResultCode.SUCCESS)
                    .setType(MsgAttackCreepProto.RequestType.ATTACK)
                    .setContent(content)
                    .setCreep(protoService.transToCreep(creep))
                    .build();

            channel.writeAndFlush(response);

            try {
                Thread.sleep(spell.getCoolDown() * 1000);
            } catch (InterruptedException e) {
                log.error("normal attack thread exception: ");
            }
        }
    }
}

