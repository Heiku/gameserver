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
import com.ljh.gamedemo.run.SiteCreepExecutorManager;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.creep.CreepBeAttackedRun;
import com.ljh.gamedemo.run.creep.CreepBeAttackedScheduleRun;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.run.user.UserDeclineMpRun;
import com.ljh.gamedemo.run.util.CountDownLatchUtil;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
        doAttack(userId, role, creep, channel);

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

        // 获取施放技能的目标野怪id
        int creepId = request.getCreepId();

        // 加锁判断野怪的状态
        synchronized (this){
           Creep creep = LocalCreepMap.getIdCreepMap().get(creepId);
           if (creep.getNum() <= 0 || creep.getHp() <= 0){

               response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                       .setResult(ResultCode.FAILED)
                       .setContent(ContentType.ATTACK_DEATH_CREEP)
                       .build();
               return response;
           }
        }

        // countDownLatch 保证用户的状态先进行操作
        CountDownLatchUtil.newLatch(1);
        UserExecutorManager.addUserTask(userId, new UserDeclineMpRun(roleId, spell, channel));

        // 判断是直接伤害还是持续伤害？
        if (spell.getSec() > 0){
            CreepBeAttackedScheduleRun r = new CreepBeAttackedScheduleRun(spell, creepId, channel, true);
            ScheduledFuture future = SiteCreepExecutorManager.getExecutor(role.getSiteId()).scheduleAtFixedRate(r, 0, 2, TimeUnit.SECONDS);
            FutureMap.futureMap.put(r.hashCode(), future);
        }else {
            SiteCreepExecutorManager.addCreepTask(role.getSiteId(), new CreepBeAttackedRun(role.getSiteId(), spell, creepId, channel, true));

        }
        return null;
    }

    /**
     * 具体的攻击野怪的操作操作
     *
     * @param role
     * @param creep
     * @param channel
     */
    private void doAttack(long userId, Role role, Creep creep, Channel channel){
        // 先选择一个普通攻击的技能
        List<Spell> spells = role.getSpellList();
        spells.sort((a, b) -> a.getSpellId().compareTo(b.getSpellId()));

        // 普通攻击技能
        Spell spell = spells.get(0);

        // 加锁判断野怪的状态
        synchronized (this){
            if (creep.getNum() <= 0 || creep.getHp() <= 0){
                response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setContent(ContentType.ATTACK_DEATH_CREEP)
                        .build();
                channel.writeAndFlush(channel);
            }
        }

        // 添加任务到用户线程
        SiteCreepExecutorManager.addCreepTask(role.getSiteId(), new CreepBeAttackedRun(role.getSiteId(), spell, creep.getCreepId(), channel, false));
        UserExecutorManager.addUserTask(userId, new UserBeAttackedRun(userId, creep, channel));
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

