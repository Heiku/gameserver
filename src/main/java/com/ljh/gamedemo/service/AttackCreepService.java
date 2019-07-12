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
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/12
 */
@Slf4j
@Service
public class AttackCreepService {

    private MsgAttackCreepProto.ResponseAttackCreep response;


    /**
     * 处理攻击野怪请求，默认采用普攻攻击的方式
     *
     * @param request
     * @param channel
     * @return
     */
    public MsgAttackCreepProto.ResponseAttackCreep attackCreep(MsgAttackCreepProto.RequestAttackCreep request, Channel channel){
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


        doAttack(role, creep, channel);

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
}

class AttackRun implements Runnable{

    private Creep creep;
    private Spell spell;
    private Channel channel;

    private ProtoService protoService = new ProtoService();

    public AttackRun(Creep creep, Spell spell, Channel channel){
        this.creep = creep;
        this.spell= spell;
        this.channel = channel;
    }

    // 这里暂时只杀一只野怪，对的
    @Override
    public void run() {
        while (true){
            Creep creep = LocalAttackCreepMap.getChannelCreepMap().get(channel);

            // 获取野怪的血量，技能的伤害值
            int hp = creep.getHp();
            int damage = spell.getDamage();

            // 野怪的生命值 -= 技能的伤害值
            if (creep.getHp() > 0){
                hp -= damage;
            }

            // 调用deathCreep() 野怪死亡相关
            if (hp < 0){
                deathCreep(channel);
                break;
            }

            // 返回攻击的消息
            //channel.writeAndFlush()
        }
    }


    /**
     * 野怪死亡，更新
     *
     * @param channel
     */
    private void deathCreep(Channel channel){
        // 更新缓存中野怪的数量 ( -1 )
        Map<Channel,Creep> channelCreepMap = LocalAttackCreepMap.getChannelCreepMap();
        Creep creep = channelCreepMap.get(channel);
        creep.setNum(creep.getNum() - 1);
        channelCreepMap.put(channel, creep);

        // update LocalAttackCreepMap channelCreepMap
        LocalAttackCreepMap.setChannelCreepMap(channelCreepMap);

        // 同时更新 LocalCreepMap中的 idCreepMap 和 siteCreepMap
        // 1.先更新idCreepMap
        Map<Integer, Creep> idCreepMap = LocalCreepMap.getIdCreepMap();
        Creep idCreep = idCreepMap.get(creep.getCreepId());
        idCreep.setNum(idCreep.getNum() - 1);
        idCreepMap.put(idCreep.getCreepId(), idCreep);

        // update LocalCreepMap idCreepMap
        LocalCreepMap.setIdCreepMap(idCreepMap);

        // 2. 再更新 siteCreepMap
        Map<Integer, List<Creep>> siteCreepMap = LocalCreepMap.getSiteCreepMap();
        List<Creep> creeps = siteCreepMap.get(creep.getSiteId());
        for (Creep c : creeps) {
            if (c.getCreepId().intValue() == creep.getCreepId().intValue()){
                c.setNum(c.getNum() - 1);
            }
        }
        siteCreepMap.put(creep.getSiteId(), creeps);

        // update LocalCreepMap siteCreepMap
        LocalCreepMap.setSiteCreepMap(siteCreepMap);


        // 所有缓存更新完毕，返回结果消息
        MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ATTACK_DEATH_CREEP)
                .setCreep(protoService.transToCreep(creep))
                .build();

        channel.writeAndFlush(response);
    }

}
