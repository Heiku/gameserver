package com.ljh.gamedemo.module.creep.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.creep.event.base.AttackCreepEvent;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.spell.bean.Spell;
import com.ljh.gamedemo.module.role.bean.RoleBuff;
import com.ljh.gamedemo.module.creep.local.LocalAttackCreepMap;
import com.ljh.gamedemo.module.creep.local.LocalCreepMap;
import com.ljh.gamedemo.module.spell.local.LocalSpellMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.role.cache.RoleBuffCache;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.SiteCreepExecutorManager;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.creep.CreepBeAttackedRun;
import com.ljh.gamedemo.run.creep.CreepBeAttackedScheduleRun;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.run.user.UserDeclineMpRun;
import com.ljh.gamedemo.module.equip.service.EquipService;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.role.service.RoleAttrService;
import com.ljh.gamedemo.module.spell.service.SpellService;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.ljh.gamedemo.common.SpellSchoolType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/12
 */
@Slf4j
@Service
public class AttackCreepService {

    /**
     * 玩家属性服务
     */
    @Autowired
    private RoleAttrService attrService;

    /**
     * 装备服务
     */
    @Autowired
    private EquipService equipService;

    /**
     * 技能服务
     */
    @Autowired
    private SpellService spellService;

    /**
     * 协议工具
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 事件发送者
     */
    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 技能协议返回
     */
    private MsgSpellProto.ResponseSpell spellResp;

    /**
     * 攻击野怪协议返回
     */
    private MsgAttackCreepProto.ResponseAttackCreep response;


    /**
     * 处理技能攻击野怪请求
     *
     * @param request       请求
     * @param channel       channel
     */
    public void spellAttackCreep(MsgAttackCreepProto.RequestAttackCreep request, Channel channel){
        // 野怪判断
        response = creepStateInterceptor(request.getCreepId());
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 攻击的野怪信息
        Creep creep = LocalCreepMap.getIdCreepMap().get(request.getCreepId());

        // 技能拦截判断
        spellResp = spellService.spellStateInterceptor(request.getSpellId());
        if (spellResp != null){
            channel.writeAndFlush(spellResp);
            return;
        }
        // 获取角色的基本信息
        Role role = LocalUserMap.getUserRoleMap().get(request.getUserId());

        // 获取施放的技能spell
        Spell spell = LocalSpellMap.getIdSpellMap().get(request.getSpellId());

        // 进行技能施放
        doSpellAttack(role, spell, creep);
    }


    /**
     * 具体的技能施放操作
     *
     * @param role      玩家信息
     * @param spell     技能信息
     * @param creep     野怪信息
     */
    private void doSpellAttack(Role role, Spell spell, Creep creep) {
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        // 初始化攻击数据
        initCreepAttack(role, creep);

        // 野怪开始攻击玩家
        creepStartAttack(role, creep);

        // 读取Buff的额外伤害
        int extra = attrService.getExtraDamage(role, spell);

        // 普攻，直接伤害
        if (spell.getCost() == 0){
            doAttack(role, creep, extra);
            return;
        }

        // 技能伤害
        // 扣蓝
        UserDeclineMpRun mpTask = new UserDeclineMpRun(role.getRoleId(), spell);
        Future<Boolean> mpFuture = UserExecutorManager.addUserCallableTask(role.getUserId(), mpTask);

        // 异步转同步，等待扣蓝任务完成
        try {
            mpFuture.sync();

            if (mpFuture.get()){
                switch (spell.getSchool()){

                    // 直接伤害
                    case DIRECT:
                        SiteCreepExecutorManager.addCreepTask(role.getSiteId(),
                                new CreepBeAttackedRun(role, creep, extra + spell.getDamage()));
                        break;

                    // 持续伤害
                    case DURATION:
                        spellDur(role, spell, creep, extra);
                        break;

                    // 护盾技能
                    case SHIELD:
                        spellShield(role, spell);
                        break;
                }
            }else {
                sendFailedMsg(channel, ContentType.DUPLICATE_SPELL_FAILED);

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 护盾技能
     *
     * @param role      玩家信息
     * @param spell     技能信息
     */
    private void spellShield(Role role, Spell spell) {
        // 获取护盾技能的具体信息
        int shield = spell.getDamage();
        int sec = spell.getSec();

        // 构建玩家 buff信息
        RoleBuff buff = new RoleBuff();
        buff.setRoleId(role.getRoleId());
        buff.setShield(shield);
        buff.setType(1);
        buff.setSec(sec);
        buff.setCreateTime(System.currentTimeMillis());

        List<RoleBuff> buffList = Optional.ofNullable(RoleBuffCache.getCache().getIfPresent(role.getRoleId()))
                .orElse(Lists.newArrayList());
        buffList.add(buff);

        // update cache
        RoleBuffCache.getCache().put(role.getRoleId(), buffList);


        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
        sendFailedMsg(channel, ContentType.ATTACK_SPELL_SUCCESS);
    }

    /**
     * 技能持续伤害
     *
     * @param role      玩家信息
     * @param spell     技能信息
     * @param creep     野怪信息
     * @param extra     额外伤害
     */
    private void spellDur(Role role, Spell spell, Creep creep, int extra){

        // 构建掉血任务
        CreepBeAttackedScheduleRun task = new CreepBeAttackedScheduleRun(role, spell, creep, extra);

        // 获取线程池，并执行
        CustomExecutor executors = SiteCreepExecutorManager.getExecutor(role.getSiteId());
        ScheduledFuture future = executors.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
        FutureMap.futureMap.put(task.hashCode(), future);
    }


    /**
     * 野怪开始从目标队列寻找攻击目标，
     *
     * @param role  玩家信息
     * @param creep 野怪信息
     */
    private void creepStartAttack(Role role, Creep creep) {
        // 获取野怪的攻击技能
        int damage = creep.getDamage();
        int coolDown = creep.getCoolDown();

        // 获取目标队列
        Deque<Long> deque = LocalAttackCreepMap.getCreepAttackedMap().get(creep.getCreepId());

        // 判断之前是否有攻击的任务
        ScheduledFuture lastFuture = LocalAttackCreepMap.getUserBeAttackedMap().get(role.getRoleId());

        // 玩家掉血任务, 第一次攻击的时候，加入玩家自动扣血，第二次攻击的时候，直接跳过
        if (lastFuture == null && !deque.isEmpty() && deque.peek() != role.getUserId().longValue()) {
            UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), damage, false);
            ScheduledFuture future = UserExecutorManager.getUserExecutor(role.getUserId()).scheduleAtFixedRate(task,
                    0, coolDown, TimeUnit.SECONDS);
            FutureMap.futureMap.put(task.hashCode(), future);
            LocalAttackCreepMap.getUserBeAttackedMap().put(role.getRoleId(), future);
        }
    }




    /**
     * 初始化野怪的攻击数据（攻击目标队列，攻击野怪的关联）
     *
     * @param role      玩家信息
     * @param creep     野怪信息
     */
    private void initCreepAttack(Role role, Creep creep) {
        // 初始化数据，关联角色和攻击的野怪，加入挑战队列中
        Deque<Long> roleIdList = Optional.ofNullable(LocalAttackCreepMap.getCreepAttackedMap().get(creep.getCreepId()))
                .orElse(Lists.newLinkedList());
        if (!roleIdList.contains(role.getRoleId())){
            roleIdList.offer(role.getRoleId());
        }

        // 保存当前的攻击野怪关联
        LocalAttackCreepMap.getCreepAttackedMap().put(creep.getCreepId(), roleIdList);
        LocalAttackCreepMap.getCurrentCreepMap().put(role.getRoleId(), creep.getCreepId());
    }



    /**
     * 具体的攻击野怪的操作操作
     *
     * @param role      玩家信息
     * @param creep     野怪信息
     */
    private void doAttack(Role role, Creep creep, int extra){
        // 先选择一个普通攻击的技能
        Spell spell = role.getSpellList().get(0);

        // 添加任务到用户线程
        // 野怪掉血任务
        SiteCreepExecutorManager.addCreepTask(role.getSiteId(),
                new CreepBeAttackedRun(role, creep, extra + spell.getDamage()));

        // 装备消耗耐久任务
        equipService.synCutEquipDurability(role);
    }




    /**
     * 玩家离开野怪攻击范围
     *
     * 1.移除玩家攻击野怪的状态
     * 2.去除玩家收到攻击的任务
     *
     * @param req           请求
     * @param channel       channel
     */
    public void stopAttack(MsgAttackCreepProto.RequestAttackCreep req, Channel channel) {

        // 获取玩家的基本信息
        Role role = LocalUserMap.userRoleMap.get(req.getUserId());

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

        sendCommonMsg(channel, ContentType.ATTACK_STOP);
    }




    /**
     * 野怪死亡的具体操作
     *
     * @param creep
     * @param role
     */
    public void doCreepDeath(Creep creep, Role role, int startHp){
        // 重置血量
        creep.setHp(startHp);
        creep.setNum(creep.getNum() - 1);
        LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

        // 野怪死亡，取消玩家自动扣血task
        ScheduledFuture future = LocalAttackCreepMap.getUserBeAttackedMap().get(role.getRoleId());
        if (future != null) {
            future.cancel(true);
        }
        LocalAttackCreepMap.getUserBeAttackedMap().remove(role.getRoleId());

        // 消息返回
        sendCreepMsg(role, creep, ContentType.ATTACK_DEATH_CREEP);

        // 事件发布
        publisher.publishEvent(new AttackCreepEvent(new BaseEvent(role, creep.getCreepId())));
    }





    /**
     * 野怪信息拦截器，校验参数
     *
     * @param creepId    野怪id
     * @return           协议返回
     */
    private synchronized MsgAttackCreepProto.ResponseAttackCreep creepStateInterceptor(long creepId){
        // 判断野怪的id是否有问题
        if (creepId <= 0 || LocalCreepMap.getIdCreepMap().get(creepId) == null){
            return combineFailedMsg(ContentType.CREEP_PARAM_EMPTY);
        }

        // 判断野怪的状态信息
        Creep creep = LocalCreepMap.getIdCreepMap().get(creepId);
        if (creep.getNum() <= 0 || creep.getHp() <= 0){
            return combineFailedMsg(ContentType.ATTACK_DEATH_CREEP);
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




    /**
     * 发送野怪消息通知
     *
     * @param creep     野怪信息
     * @param msg       消息
     */
    public void sendCreepMsg(Role role, Creep creep, String msg){
        // 获取对应攻击的玩家channel
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setType(MsgAttackCreepProto.RequestType.ATTACK)
                .setResult(ResultCode.SUCCESS)
                .setContent(msg)
                .setCreep(protoService.transToCreep(creep))
                .build();
        channel.writeAndFlush(response);
    }




    /**
     * 发送失败消息
     *
     * @param channel   channel
     * @param msg       消息
     */
    private void sendFailedMsg(Channel channel, String msg){
        response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(response);
    }




    /**
     * 发送公共消息
     *
     * @param channel   channel
     * @param msg       消息
     */
    private void sendCommonMsg(Channel channel, String msg){
        response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgAttackCreepProto.RequestType.STOP)
                .setContent(msg)
                .build();
        channel.writeAndFlush(response);
    }




    /**
     * 构造失败消息
     *
     * @param msg       消息
     * @return          协议返回
     */
    private MsgAttackCreepProto.ResponseAttackCreep combineFailedMsg(String msg){
        return  MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
    }
}

