package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalCreepMap;
import com.ljh.gamedemo.local.LocalSpellMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.run.ExecutorManager;
import com.ljh.gamedemo.run.NormalAttackRun;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/12
 */
@Slf4j
@Service
public class AttackCreepService {

    @Autowired
    private ProtoService protoService;

    @Autowired
    private DeathCreepService deathCreepService;

    private MsgAttackCreepProto.ResponseAttackCreep response;

    /**
     * 处理攻击野怪请求，默认采用普攻攻击的方式
     *
     * @param request
     * @param channel
     * @return
     */
    public MsgAttackCreepProto.ResponseAttackCreep attackCreep(MsgAttackCreepProto.RequestAttackCreep request, Channel channel){

        // 先进行角色状态，野怪状态的拦截判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        response = creepStateInterceptor(request);
        if (response != null){
            return response;
        }


        // 获取角色的基本信息
        long userId = request.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);
        long roleId = role.getRoleId();
        List<Spell> spells = LocalSpellMap.getRoleSpellMap().get(roleId);
        role.setSpellList(spells);

        // 获取野怪的基本信息
        int creepId = request.getCreepId();
        Creep creep = LocalCreepMap.getIdCreepMap().get(creepId);

        // 初始化数据，关联角色和攻击的野怪
        LocalAttackCreepMap.channelCreepMap.put(channel, creep);

        // 攻击野怪
        doAttack(role, creep, channel);

        return null;
    }


    /**
     * 处理技能攻击野怪请求
     *
     * @param request
     * @param channel
     * @return
     */
    public MsgAttackCreepProto.ResponseAttackCreep spellAttackCreep(MsgAttackCreepProto.RequestAttackCreep request, Channel channel){
        // 先进行角色状态，野怪状态的拦截判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 技能拦截判断
        response = spellStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 获取角色的基本信息
        long userId = request.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);

        log.info(role.getName() + " attack creep by spell");

        long roleId = role.getRoleId();
        List<Spell> spells = LocalSpellMap.getRoleSpellMap().get(roleId);
        role.setSpellList(spells);


        // 获取施放的技能spell
        int spellId = request.getSpellId();
        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);

        // 根据正在攻击的野怪
        Creep creep = LocalAttackCreepMap.getChannelCreepMap().get(channel);
        spellAttack(spell, creep, role, channel);

        return null;
    }

    /**
     * 具体的攻击野怪的操作操作
     *
     * @param role
     * @param creep
     * @param channel
     */
    private void doAttack(Role role, Creep creep, Channel channel){
        // 先选择一个普通攻击的技能
        List<Spell> spells = role.getSpellList();
        spells.sort((a, b) -> a.getSpellId().compareTo(b.getSpellId()));

        // 普通攻击技能
        Spell spell = spells.get(0);

        //Thread
        ExecutorManager.getExecutors().execute(new NormalAttackRun(creep, spell, channel, true));
    }


    /**
     * 使用技能攻击野怪
     * 1.计算技能cd时间
     * 2.计算mp值是不是足以施放技能
     * 3.攻击成功，野怪掉血，记录缓存
     * 4.技能消耗mp，记录缓存
     *
     * @param spell
     * @param creep
     * @param channel
     */
    private void spellAttack(Spell spell, Creep creep, Role role,  Channel channel){
        int startHp = creep.getHp();

        // 获取施放技能的cd，蓝耗
        int cd = spell.getCoolDown();
        int cost = spell.getCost();

        // 用户当前mp值，判断是否足够施放技能
        int lastMp = role.getMp();
        if (lastMp < cost){
            response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setType(MsgAttackCreepProto.RequestType.SPELL)
                    .setContent(ContentType.ATTACK_SPELL_MP_NO_ENOUGH)
                    .build();
            channel.writeAndFlush(response);
            return;
        }

        // 施放技能前，进行一次时间戳判断，判断是否在cd时间内
        long last = 0L;
        if (LocalAttackCreepMap.channelTimeStampMap.containsKey(channel)){
            last = LocalAttackCreepMap.channelTimeStampMap.get(channel);
        }
        long now = System.currentTimeMillis();
        if (last > 0){
            long t = now - last;

            // 还在cd中，通知用户等待时间点
            if (t < cd * 1000){
                double interval = Math.floor((cd * 1000 - t) / 1000);

                response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setType(MsgAttackCreepProto.RequestType.SPELL)
                        .setContent(ContentType.ATTACK_SPELL_CD + interval + "秒\n")
                        .build();

                channel.writeAndFlush(response);
                return;
            }
        }
        // 记录下本次施放技能的时间戳
        LocalAttackCreepMap.getChannelTimeStampMap().put(channel, now);



        Creep c = LocalAttackCreepMap.getChannelCreepMap().get(channel);
        // 获取野怪血量，技能伤害
        int hp = c.getHp();
        int damage = spell.getDamage();

        if (c.getHp() > 0){
            hp -= damage;
        }
        log.info("spell attack creep info: " + c.getHp());

        // 更新野怪的生命值，同步到缓存中去
        c.setHp(hp);
        LocalAttackCreepMap.getChannelCreepMap().put(channel, c);
        if (hp < 0){
            deathCreepService.deathCreep(channel, startHp);
        }

        // 技能消耗mp，更新用户的mp值
        long userId = role.getUserId();
        role.setMp(lastMp - cost);

        // 用户mp更新成功，更新role cache
        LocalUserMap.userRoleMap.put(userId, role);

        response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setType(MsgAttackCreepProto.RequestType.SPELL)
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ATTACK_SPELL_SUCCESS)
                .setCreep(protoService.transToCreep(c))
                .build();

        channel.writeAndFlush(response);
    }


    /**
     * 用户状态拦截器，检验参数
     *
     * @param requestAttackCreep
     * @return
     */
    private MsgAttackCreepProto.ResponseAttackCreep userStateInterceptor(MsgAttackCreepProto.RequestAttackCreep requestAttackCreep){
        // 用户id标识判断
        long userId = requestAttackCreep.getUserId();
        if (userId <= 0){
            return MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }
        // 找不到对应的角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }

        return null;
    }


    /**
     * 野怪信息拦截器，校验参数
     *
     * @param requestAttackCreep
     * @return
     */
    private MsgAttackCreepProto.ResponseAttackCreep creepStateInterceptor(MsgAttackCreepProto.RequestAttackCreep requestAttackCreep){
        if (requestAttackCreep.getSpellId() > 0){
            return null;
        }

        // 判断野怪的id是否有问题
        int creepId = requestAttackCreep.getCreepId();
        if (creepId <= 0){
            return MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.CREEP_PARAM_EMPTY)
                    .build();
        }

        Creep creep = LocalCreepMap.getIdCreepMap().get(creepId);
        if (creep == null){
            return MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.CREEP_EMPTY)
                    .build();
        }

        return null;
    }


    /**
     * 技能信息查询拦截
     */
    private MsgAttackCreepProto.ResponseAttackCreep spellStateInterceptor(MsgAttackCreepProto.RequestAttackCreep requestAttackCreep){

        // 判断技能信息是否存在的问题
        int spellId = requestAttackCreep.getSpellId();
        if (spellId <= 0){
            return MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setType(MsgAttackCreepProto.RequestType.SPELL)
                    .setContent(ContentType.ATTACK_SPELL_EMPTY)
                    .build();
        }

        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);
        if (spell == null){
            return MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setType(MsgAttackCreepProto.RequestType.SPELL)
                    .setContent(ContentType.ATTACK_SPELL_NOT_FOUND)
                    .build();
        }

        return null;
    }
}

