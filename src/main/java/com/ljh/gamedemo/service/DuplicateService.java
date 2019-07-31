package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalDuplicateMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.DuplicateProto;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.DuplicateManager;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.boss.BossBeAttackedRun;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        // 记录下挑战boss的时间点
        long now = System.currentTimeMillis();
        LocalAttackCreepMap.getDupTimeStampMap().put(role.getRoleId(), now);

        // Boss开始主动攻击玩家
        // 根据Boss的普攻攻击间隔，玩家自动扣血
        // TODO: 副本组队优化
        // 这里暂时是一个玩家进来副本，那么就对玩家进行攻击吗？
        // 还有，这里到时如果是组队的话，那该怎么办呢？
        // 再进入副本的时候，判断玩家队伍的角色分配，优先攻打战士等职业
        userBeAttackedByBoss(role, tmpDup, channel);


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
        if (response == null){
            return response;
        }
        response = bossStateInterceptor(request);
        if (response == null){
            return response;
        }
        // 获取基本信息
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());

        // 判断boss的血量信息
        response = syngetBossInfo(dup);
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
    private synchronized MsgDuplicateProto.ResponseDuplicate  syngetBossInfo(Duplicate dup) {
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

    public MsgDuplicateProto.ResponseDuplicate stopAttack(MsgDuplicateProto.RequestDuplicate request, Channel channel) {
        return null;
    }


    /**
     * 玩家开始被boss自动攻击，开始收到伤害
     *
     * @param role
     * @param dup
     * @param channel
     */
    public void userBeAttackedByBoss(Role role, Duplicate dup, Channel channel){
        // 获取副本中的 Boss 普攻技能
        // 取出boss，准备执行boss的自动攻击技能
        Boss boss = dup.getBosses().get(0);
        List<BossSpell> spells = boss.getSpellList();
        log.info("boss: " + boss.getName() + " 的技能列表为：" + spells);
        BossSpell aSpell = boss.getSpellList().get(0);

        // 新建任务
        UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), aSpell.getDamage(), channel);

        // 取出用户线程池
        CustomExecutor userExecutor = UserExecutorManager.getUserExecutor(role.getUserId());

        // 运行
        ScheduledFuture future = userExecutor.scheduleAtFixedRate(task, 0, aSpell.getCd(), TimeUnit.SECONDS);

        // 存放 Boss 自动攻击的任务信息
        LocalAttackCreepMap.getUserBeAttackedMap().put(role.getRoleId(), future);
        FutureMap.futureMap.put(task.hashCode(), future);
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

        return tmp;
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

        long dupId = request.getDupId();
        if (dupId <= 0){
           return response;
        }

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


}
