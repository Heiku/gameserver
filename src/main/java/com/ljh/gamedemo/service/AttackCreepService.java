package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.entity.dto.RoleBuff;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalCreepMap;
import com.ljh.gamedemo.local.LocalSpellMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.RoleBuffCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.SiteCreepExecutorManager;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.creep.CreepBeAttackedRun;
import com.ljh.gamedemo.run.creep.CreepBeAttackedScheduleRun;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.run.user.UserDeclineMpRun;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/7/12
 */
@Slf4j
@Service
public class AttackCreepService {

    private MsgAttackCreepProto.ResponseAttackCreep response;

    /**
     * 装备服务
     */
    @Autowired
    private EquipService equipService;

    /**
     * 协议工具
     */
    @Autowired
    private ProtoService protoService;


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
        long creepId = request.getCreepId();
        Creep creep = LocalCreepMap.getIdCreepMap().get(creepId);

        // 初始化数据，关联角色和攻击的野怪
        Deque<Long> roleIdList = LocalAttackCreepMap.getCreepAttackedMap().get(creepId);
        if (roleIdList == null){
            roleIdList = new LinkedList<>();
            roleIdList.offer(roleId);
        }
        if (!roleIdList.contains(roleId)){
            roleIdList.offer(roleId);
        }
        LocalAttackCreepMap.getCreepAttackedMap().put(creepId, roleIdList);

        LocalAttackCreepMap.getCurrentCreepMap().put(roleId, creepId);

        // 加锁判断野怪野怪状态
        synchronized (this){
            if (creep.getNum() <= 0 || creep.getHp() <= 0){
                response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setContent(ContentType.ATTACK_DEATH_CREEP)
                        .build();
                channel.writeAndFlush(channel);
                return null;
            }
        }

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
        long creepId = request.getCreepId();

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

        UserDeclineMpRun task = new UserDeclineMpRun(roleId, spell);
        Future<Boolean> mpFuture = UserExecutorManager.addUserCallableTask(userId, task);

        if (mpFuture != null && mpFuture.isSuccess()) {
            // 判断是直接伤害还是持续伤害？
            if (spell.getSec() > 0) {
                CreepBeAttackedScheduleRun r = new CreepBeAttackedScheduleRun(spell, creepId, channel, role);
                CustomExecutor executors = SiteCreepExecutorManager.getExecutor(role.getSiteId());
                ScheduledFuture future = executors.scheduleAtFixedRate(r, 0, 2, TimeUnit.SECONDS);
                FutureMap.futureMap.put(r.hashCode(), future);
            } else {
                SiteCreepExecutorManager.addCreepTask(role.getSiteId(), new CreepBeAttackedRun(role, spell, creepId, channel, false));

            }
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

        // 添加任务到用户线程
        // 野怪掉血任务
        SiteCreepExecutorManager.addCreepTask(role.getSiteId(), new CreepBeAttackedRun(role , spell, creep.getCreepId(), channel, true));


        // 玩家掉血任务, 第一次攻击的时候，加入玩家自动扣血，第二次攻击的时候，直接跳过
        // 掉血任务，当野怪死亡的时候 或 玩家死亡 停止
        ScheduledFuture lastFuture = LocalAttackCreepMap.getUserBeAttackedMap().get(userId);

        Deque<Long> deque = LocalAttackCreepMap.getCreepAttackedMap().get(creep.getCreepId());
        if (lastFuture == null && !deque.isEmpty() && deque.peek() != userId) {
            UserBeAttackedRun task = new UserBeAttackedRun(userId, spell.getDamage(), false);
            ScheduledFuture future = UserExecutorManager.getUserExecutor(userId).scheduleAtFixedRate(task,
                    0, spell.getCoolDown(), TimeUnit.SECONDS);
            FutureMap.futureMap.put(task.hashCode(), future);
            LocalAttackCreepMap.getUserBeAttackedMap().put(role.getRoleId(), future);
        }

        // 装备消耗耐久任务
        equipService.synCutEquipDurability(role);
    }


    /**
     * 用户主动技能，施放技能Buff
     *
     * @param request
     * @param channel
     * @return
     */
    public MsgAttackCreepProto.ResponseAttackCreep spellToSave(MsgAttackCreepProto.RequestAttackCreep request, Channel channel) {

        // user.spell interceptor
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }
        response = spellStateInterceptor(request);
        if (response != null){
            return response;
        }

        // gain base data
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        Spell spell = LocalSpellMap.getIdSpellMap().get(request.getSpellId());

        // transform bean entity
        int shield = spell.getDamage();
        int sec = spell.getSec();
        RoleBuff buff = new RoleBuff();
        buff.setRoleId(role.getRoleId());
        buff.setShield(shield);
        buff.setType(1);
        buff.setSec(sec);
        buff.setCreateTime(System.currentTimeMillis());

        List<RoleBuff> buffList = RoleBuffCache.getCache().getIfPresent(role.getRoleId());
        if (buffList == null || buffList.isEmpty()){
            buffList = new ArrayList<>();
        }
        buffList.add(buff);

        // update cache
        RoleBuffCache.getCache().put(role.getRoleId(), buffList);

        return MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setContent(ContentType.ATTACK_SPELL_SUCCESS)
                .setResult(ResultCode.FAILED)
                .setType(MsgAttackCreepProto.RequestType.SAVE)
                .build();
    }



    /**
     * 玩家离开野怪攻击范围
     *
     * 1.移除玩家攻击野怪的状态
     * 2.去除玩家收到攻击的任务
     *
     * @param requestAttackCreep
     * @param channel
     * @return
     */
    public MsgAttackCreepProto.ResponseAttackCreep stopAttack(MsgAttackCreepProto.RequestAttackCreep requestAttackCreep, Channel channel) {

        // 获取玩家的基本信息
        long userId = requestAttackCreep.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);

        // 获取退出攻击的野怪目标
        Long creepId = LocalAttackCreepMap.getCurrentCreepMap().get(role.getRoleId());

        // 获取当前野怪的攻击队伍
        Deque<Long> deque = LocalAttackCreepMap.getCreepAttackedMap().get(creepId);
        if (deque != null){
            deque.remove(role.getRoleId());
        }

        // 取消玩家收到伤害的定时任务
        ScheduledFuture future = LocalAttackCreepMap.getUserBeAttackedMap().get(role.getRoleId());
        if (future != null){
            future.cancel(true);
        }

        return MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgAttackCreepProto.RequestType.STOP)
                .setContent(ContentType.ATTACK_STOP)
                .build();
    }



    /**
     * 用户状态拦截器，检验参数
     *
     * @param requestAttackCreep    请求
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
     * @param requestAttackCreep    请求
     * @return
     */
    private MsgAttackCreepProto.ResponseAttackCreep creepStateInterceptor(MsgAttackCreepProto.RequestAttackCreep requestAttackCreep){
        if (requestAttackCreep.getSpellId() > 0){
            return null;
        }

        // 判断野怪的id是否有问题
        long creepId = requestAttackCreep.getCreepId();
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



    /**
     * 消息返回
     *
     * @param role
     */
    public void responseAttacked(Role role, String msg){
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep
                .newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgAttackCreepProto.RequestType.ATTACK)
                .setContent(msg)
                .setRole(protoService.transToRole(role))
                .build();
        channel.writeAndFlush(response);
    }

}

