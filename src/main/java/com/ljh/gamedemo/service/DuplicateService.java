package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalDuplicateMap;
import com.ljh.gamedemo.local.LocalSpellMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.DuplicateProto;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.DuplicateManager;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.boss.BossBeAttackedRun;
import com.ljh.gamedemo.run.boss.BossBeAttackedScheduleRun;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.run.user.UserDeclineMpRun;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/7/30
 *
 * 副本相关操作
 */

@Service
@Slf4j
public class DuplicateService {

    private ProtoService protoService = ProtoService.getInstance();

    private MsgDuplicateProto.ResponseDuplicate response;

    @Autowired
    private EquipService equipService;

    /**
     * 获取当前的所有副本信息
     *
     * @param request
     * @return
     */
    public MsgDuplicateProto.ResponseDuplicate getDuplicate(MsgDuplicateProto.RequestDuplicate request){
        // 玩家角色状态判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 获取当前的副本列表
        List<Duplicate> duplicates = new ArrayList<>();
        LocalDuplicateMap.getDuplicateMap().forEach((k, v)  -> duplicates.add(v));

        // 返回消息
        List<DuplicateProto.Duplicate> resList = protoService.transToDuplicateList(duplicates);

        return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgDuplicateProto.RequestType.DUPLICATE)
                .setContent(ContentType.DUPLICATE_ALL)
                .addAllDuplicate(resList)
                .build();
    }

    /**
     * 用户进入副本内
     *
     * 1.玩家的角色状态判断
     * 2.构建一个临时的副本
     * 3.野怪开始自动攻击玩家
     *
     *
     * @param request
     * @return
     */
    public MsgDuplicateProto.ResponseDuplicate enterDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel) {
        // 玩家角色状态判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 副本信息判断
        response = duplicateStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 信息初始化
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

        // 创建临时副本信息, 便于后期进行对象释放
        Duplicate tmpDup = createTmpDuplicate(request, role);

        // 当前副本线程池
        CustomExecutor executor = DuplicateManager.getExecutor(role.getRoleId());

        // 先判断是否时队伍
        // 这里如果时组队的话，那么在进入场景的时候，就先根据队伍中角色的类别，进行排序进入队列
        // 记录 Boss的攻击目标队列
        recordRoleAttacked(role, tmpDup);

        // Boss开始主动攻击玩家
        // 根据Boss的普攻攻击间隔，玩家自动扣血
        // TODO: 副本组队优化，同时Boss的技能释放
        // 到这里，已经有 Boss的攻击队列
        // 那么在进入地图的时候，Boss 就回从目标队列头中找到攻击目标，并开始执行攻击掉血任务
        userBeAttackedByBoss(tmpDup, channel);


        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.DUPLICATE_ENTER_SUCCESS)
                .setType(MsgDuplicateProto.RequestType.ENTER)
                .addAllBoss(protoService.transToBossList(tmpDup.getBosses()))
                .build();
        channel.writeAndFlush(response);

        return null;
    }


    /**
     * 玩家使用技能攻击 Boss
     *
     * @param request
     * @param channel
     * @return
     */
    public MsgDuplicateProto.ResponseDuplicate challengeDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel) {
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }
        // 获取基本信息
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        role.setSpellList(LocalSpellMap.getRoleSpellMap().get(role.getRoleId()));

        response = bossStateInterceptor(request);
        if (response != null){
            return response;
        }

        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());

        // 判断boss的血量信息
        response = synGetBossInfo(dup);
        if (response != null){
            return response;
        }

        // Boss 存活,获取 Boss的信息
        Boss boss = dup.getBosses().get(0);

        // 获取玩家的攻击力，然后找对对应的boss进行掉血
        doAttackBoss(role, boss, channel);

        return null;
    }

    /**
     * 使用技能攻击 Boss
     *
     * @param request
     * @param channel
     * @return
     */
    public MsgDuplicateProto.ResponseDuplicate spellBoss(MsgDuplicateProto.RequestDuplicate request, Channel channel){
        // 用户状态判断
        response = userStateInterceptor(request);
        if (response != null) {
            return response;
        }
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        role.setSpellList(LocalSpellMap.getRoleSpellMap().get(role.getRoleId()));

        // 技能判断
        response = spellStateInterceptor(request);
        if (response != null) {
            return response;
        }
        Spell spell = LocalSpellMap.getIdSpellMap().get(request.getSpellId());

        // 副本判断
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());
        response = synGetBossInfo(dup);
        if (response != null){
            return response;
        }

        // 具体攻击 Boss 操作
        doSpellAttack(role, spell, dup, channel);

        return null;
    }

    /**
     * 玩家技能对 Boss 造成伤害
     *
     * @param role
     * @param spell
     * @param channel
     */
    private void doSpellAttack(Role role, Spell spell, Duplicate dup, Channel channel) {
        Boss boss = dup.getBosses().get(0);

        // 先进行扣蓝操作
        UserDeclineMpRun mpTask = new UserDeclineMpRun(role.getRoleId(), spell);
        Future<Boolean> mpFuture = UserExecutorManager.addUserCallableTask(role.getUserId(), mpTask);

        // 异步转同步，等待扣蓝任务完成
        try {
            mpFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 判断是否扣蓝成功
        try {
            // 只有当用户扣蓝成功，再继续进行
            if (mpFuture.get()) {
                // 判断持续伤害?
                if (spell.getSec() > 0) {
                    // 新建掉血任务，任务提交
                    BossBeAttackedScheduleRun scheduleTask = new BossBeAttackedScheduleRun(role, spell, boss, channel);
                    CustomExecutor executor = DuplicateManager.getExecutor(role.getRoleId());

                    // 记录任务，等待取消
                    ScheduledFuture future = executor.scheduleAtFixedRate(scheduleTask, 0, 2, TimeUnit.SECONDS);
                    FutureMap.futureMap.put(scheduleTask.hashCode(), future);
                } else {
                    // 直接伤害技能
                    BossBeAttackedRun bossTask = new BossBeAttackedRun(role, spell, boss, false, channel);
                    DuplicateManager.addDupTask(role.getRoleId(), bossTask);
                }
            } else {
                sendFailedMsg(ContentType.DUPLICATE_SPELL_FAILED, channel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 玩家普通攻击对 Boss 造成伤害
     *
     * @param role
     * @param boss
     * @param channel
     */
    private void doAttackBoss(Role role, Boss boss,  Channel channel) {
        // 获取普功技能
        List<Spell> spells = role.getSpellList();
        spells.sort((a, b) -> a.getSpellId().compareTo(b.getSpellId()));
        Spell aSpell = spells.get(0);

        // 获取临时副本线程
        // 这里的获取线程池的 id，到时需要修改，已能够适配组队
        BossBeAttackedRun task = new BossBeAttackedRun(role, aSpell, boss, true, channel);
        DuplicateManager.addDupTask(role.getRoleId(), task);

        // 玩家装备耐久消耗
        equipService.synCutEquipDurability(role);
    }

    /**
     * 加锁判断boss的状态
     *
     * @param dup
     * @return
     */
    private synchronized MsgDuplicateProto.ResponseDuplicate  synGetBossInfo(Duplicate dup) {
        // 判断存活的boss，因为死亡的boss 会被移除 duplicate 的 bossList
        Boss boss = dup.getBosses().get(0);
        if (boss == null){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.DUPLICATE_BOSS_HAD_DEATH)
                    .build();
        }
        return null;
    }

    /**
     * 玩家离开boss的攻击范围，停止受到伤害
     *
     * @param request
     * @param channel
     * @return
     */
    public MsgDuplicateProto.ResponseDuplicate stopAttack(MsgDuplicateProto.RequestDuplicate request, Channel channel) {
        // 获取请求的基本信息
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

        Duplicate nowDup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());

        // 将当前玩家移除 Boss的目标队列
        removeAttackedQueue(role);

        // 移除玩家的被 Boss攻击的掉血任务
        ScheduledFuture future = LocalAttackCreepMap.getUserBeAttackedMap().get(role.getRoleId());
        future.cancel(true);

        // 重新选取目标
        userBeAttackedByBoss(nowDup, channel);

        return null;
    }


    /**
     * 玩家离开当前副本，退出
     *
     * @param request
     * @param channel
     * @return
     */
    public MsgDuplicateProto.ResponseDuplicate leaveDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel){
        // 玩家信息判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 副本信息判断
        response = duplicateStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 获取基本信息
        long userId = request.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());

        // TODO：后续组队的时候，进行重新判断
        // 移除目标队列
        removeAttackedQueue(role);

        // 移除挑战时间
        LocalAttackCreepMap.getDupTimeStampMap().remove(role.getRoleId());

        // 移除玩家的掉血任务
        ScheduledFuture future = LocalAttackCreepMap.getUserBeAttackedMap().get(role.getRoleId());
        future.cancel(true);

        // 移除副本-玩家关联
        LocalAttackCreepMap.getCurDupMap().remove(role.getRoleId());

        // 副本移除
        // 后续判断队伍中是否还有玩家，如果没有玩家的话，释放资源
        // 如果有的话，野怪重选目标进行攻击
        // 暂时单人刷副本的话，直接销毁副本信息，回收资源
        LocalAttackCreepMap.getBossAttackedMap().remove(dup);
        dup = null;

        // 最后释放暂用的副本线程池
        DuplicateManager.unBindDupExecutor(role.getRoleId());

        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.DUPLICATE_LEAVE_SUCCESS)
                .build();

        return response;
    }


    /**
     * 玩家开始被boss自动攻击，开始收到伤害
     *
     * @param dup
     * @param channel
     */
    public void userBeAttackedByBoss(Duplicate dup, Channel channel){
        Role role = null;

        // 取出 Boss 的攻击目标
        Queue<Long> aimedQueue = LocalAttackCreepMap.getBossAttackedMap().get(dup);
        if (aimedQueue != null && !aimedQueue.isEmpty()){
            long aimed = aimedQueue.peek();
            role = LocalUserMap.idRoleMap.get(aimed);
        }

        if (role != null) {
            // 获取副本中的 Boss 普攻技能
            // 取出boss，准备执行boss的自动攻击技能
            Boss boss = dup.getBosses().get(0);
            List<BossSpell> spells = boss.getSpellList();
            log.info("boss: " + boss.getName() + " 的技能列表为：" + spells);
            BossSpell aSpell = boss.getSpellList().get(0);

            // 新建任务
            UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), aSpell.getDamage(), false);

            // 取出用户线程池
            CustomExecutor userExecutor = UserExecutorManager.getUserExecutor(role.getUserId());

            // 运行
            ScheduledFuture future = userExecutor.scheduleAtFixedRate(task, 0, aSpell.getCd(), TimeUnit.SECONDS);

            // 存放 Boss 自动攻击的任务信息
            LocalAttackCreepMap.getUserBeAttackedMap().put(role.getRoleId(), future);
            FutureMap.futureMap.put(task.hashCode(), future);
        }
    }


    /**
     * 用于创建临时副本信息，便于后续回收
     *
     * @param request
     * @return
     */
    private Duplicate createTmpDuplicate(MsgDuplicateProto.RequestDuplicate request, Role role) {
        long dupId = request.getDupId();

        // 读取本地的基本副本信息
        Duplicate data = LocalDuplicateMap.getDuplicateMap().get(dupId);
        Duplicate tmp = new Duplicate();

        // 加载到新创建的副本对象中
        BeanUtils.copyProperties(data, tmp);

        // 绑定线程池，同时绑定副本对象
        DuplicateManager.bindDupExecutor(role.getRoleId());
        LocalAttackCreepMap.getCurDupMap().put(role.getRoleId(), tmp);

        // 记录下挑战boss的时间点
        long now = System.currentTimeMillis();
        LocalAttackCreepMap.getDupTimeStampMap().put(role.getRoleId(), now);

        return tmp;
    }


    /**
     * 用于记录Boss 的攻击目标队列
     *
     * @param role
     * @param dup
     */
    private void recordRoleAttacked(Role role, Duplicate dup){
        // 记录下进入Boss的次序
        // 后续在这里修改 组队的时候 Boss 的攻击判断
        Queue<Long> roldIdList = LocalAttackCreepMap.getBossAttackedMap().get(dup);
        if (roldIdList == null){
            roldIdList = new LinkedList<>();
            roldIdList.offer(role.getRoleId());
        }
        if (!roldIdList.contains(role.getRoleId())){
            roldIdList.offer(role.getRoleId());
        }
        LocalAttackCreepMap.getBossAttackedMap().put(dup, roldIdList);
    }

    /**
     * 将当前玩家移除 Boss 的目标队列
     *
     * @param role
     */
    private void removeAttackedQueue(Role role){
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());
        // 移除目标队列
        Queue<Long> queue = LocalAttackCreepMap.getBossAttackedMap().get(dup);
        if (queue != null){
            queue.remove(role.getRoleId());
        }
    }


    /**
     * 副本信息拦截器
     *
     * @param request
     * @return
     */
    private MsgDuplicateProto.ResponseDuplicate duplicateStateInterceptor(MsgDuplicateProto.RequestDuplicate request) {
        MsgDuplicateProto.ResponseDuplicate response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(ContentType.DUPLICATE_EMPTY)
                .build();

        // 副本 id 参数有误
        long dupId = request.getDupId();
        if (dupId <= 0){
           return response;
        }

        // 找不到该副本
        Duplicate d = LocalDuplicateMap.getDuplicateMap().get(dupId);
        return d == null ? response : null;
    }


    /**
     * 用户状态拦截器，检验参数
     *
     * @param requestEquip
     * @return
     */
    private MsgDuplicateProto.ResponseDuplicate userStateInterceptor(MsgDuplicateProto.RequestDuplicate requestEquip){
        // 用户id标识判断
        long userId = requestEquip.getUserId();
        if (userId <= 0){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }
        // 找不到对应的角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }
        return null;
    }

    private MsgDuplicateProto.ResponseDuplicate bossStateInterceptor(MsgDuplicateProto.RequestDuplicate request){
        // 获取玩家信息
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

        // 获取对应的场景信息
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());
        if (dup == null){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setType(MsgDuplicateProto.RequestType.CHALLENGE)
                    .setContent(ContentType.DUPLICATE_ENTER_NOT_FOUND)
                    .build();
        }

        // 找不到该 boss
        Boss boss = null;
        for (Boss b : dup.getBosses()) {
            if (b.getId() == request.getBossId()){
                boss = b;
                break;
            }
        }
        if (boss == null){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setType(MsgDuplicateProto.RequestType.CHALLENGE)
                    .setContent(ContentType.DUPLICATE_BOSS_NOT_FOUND)
                    .build();
        }
        return null;
    }


    /**
     * 技能信息查询拦截
     */
    private MsgDuplicateProto.ResponseDuplicate spellStateInterceptor(MsgDuplicateProto.RequestDuplicate requestAttackCreep){

        // 判断技能信息是否存在的问题
        int spellId = requestAttackCreep.getSpellId();
        if (spellId <= 0){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setType(MsgDuplicateProto.RequestType.SPELL)
                    .setContent(ContentType.ATTACK_SPELL_EMPTY)
                    .build();
        }

        // 找不到该技能
        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);
        if (spell == null){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setType(MsgDuplicateProto.RequestType.SPELL)
                    .setContent(ContentType.ATTACK_SPELL_NOT_FOUND)
                    .build();
        }
        return null;
    }

    /**
     * 返回失败的消息
     *
     * @param content
     * @param channel
     */
    private void sendFailedMsg(String content, Channel channel) {
        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        channel.writeAndFlush(response);
    }

}
