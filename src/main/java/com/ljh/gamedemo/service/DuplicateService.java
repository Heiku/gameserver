package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.entity.dto.RoleAttr;
import com.ljh.gamedemo.local.*;
import com.ljh.gamedemo.local.cache.GroupCache;
import com.ljh.gamedemo.local.cache.RoleAttrCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.DuplicateProto;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.DuplicateManager;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.dup.BossBeAttackedRun;
import com.ljh.gamedemo.run.dup.BossBeAttackedScheduleRun;
import com.ljh.gamedemo.run.dup.BossDoAttackedRun;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserDeclineMpRun;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private EquipService equipService;

    @Autowired
    private SpellService spellService;

    @Autowired
    private ProtoService protoService;



    private MsgUserInfoProto.ResponseUserInfo userResp;

    private MsgSpellProto.ResponseSpell spellResp;

    private MsgDuplicateProto.ResponseDuplicate dupResp;

    /**
     * 获取当前的所有副本信息
     *
     * @param request
     * @return
     */
    public void getDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel){
        // 玩家角色状态判断
        userResp = userService.userStateInterceptor(request.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 获取当前的副本列表
        List<Duplicate> duplicates = new ArrayList<>();
        LocalDuplicateMap.getDuplicateMap().forEach((k, v)  -> duplicates.add(v));

        // 返回消息
        List<DuplicateProto.Duplicate> resList = protoService.transToDuplicateList(duplicates);

        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgDuplicateProto.RequestType.DUPLICATE)
                .setContent(ContentType.DUPLICATE_ALL)
                .addAllDuplicate(resList)
                .build();
        channel.writeAndFlush(dupResp);
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
    public void enterDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel) {
        // 玩家角色状态判断
        userResp = userService.userStateInterceptor(request.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 副本信息判断
        dupResp = duplicateStateInterceptor(request);
        if (dupResp != null){
            channel.writeAndFlush(dupResp);
            return;
        }
        // 信息初始化
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

        // 创建临时副本信息, 便于后期进行对象释放
        Duplicate tmpDup = createTmpDuplicate(request, role);

        // 建立挑战者队列（玩家 / 队伍）
        recordRoleAttacked(role, tmpDup);

        // Boss开始主动攻击玩家
        doBossAttacked(tmpDup);


        // 这里应该channelGroup 发送
        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.DUPLICATE_ENTER_SUCCESS)
                .setType(MsgDuplicateProto.RequestType.ENTER)
                .addAllBoss(protoService.transToBossList(tmpDup.getBosses()))
                .build();
        sendDuplicateMsg(role, dupResp);
    }



    /**
     * 使用技能攻击 Boss
     *
     * @param request
     * @param channel
     * @return
     */
    public void spellBoss(MsgDuplicateProto.RequestDuplicate request, Channel channel){
        // 用户状态判断
        userResp = userService.userStateInterceptor(request.getUserId());
        if (userResp != null) {
            channel.writeAndFlush(userResp);
            return;
        }
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        // role.setSpellList(LocalSpellMap.getRoleSpellMap().get(role.getRoleId()));

        // 技能判断
        spellResp = spellService.spellStateInterceptor(request.getSpellId());
        if (spellResp != null) {
            channel.writeAndFlush(spellResp);
            return;
        }
        Spell spell = LocalSpellMap.getIdSpellMap().get(request.getSpellId());

        // 副本判断
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(getBindId(role));
        dupResp = synGetBossInfo(dup);
        if (dupResp != null){
            channel.writeAndFlush(dupResp);
            return;
        }

        // 具体攻击 Boss 操作
        doSpellAttack(role, spell, dup, channel);
    }

    /**
     * 玩家技能对 Boss 造成伤害
     *
     * @param role
     * @param spell
     * @param channel
     */
    private void doSpellAttack(Role role, Spell spell, Duplicate dup, Channel channel) {
        int extra = 0;
        // 获取 Buff 加成的额外伤害
        RoleAttr attr = RoleAttrCache.getRoleAttrMap().get(role.getRoleId());
        if (attr != null){
            extra = spell.getCost() == 0 ? attr.getDamage() : attr.getSp();
        }

        Boss boss = dup.getBosses().get(0);

        // 普攻类型
        if (spell.getCost() == 0) {
            // 获取临时副本线程
            // 这里的获取线程池的 id，到时需要修改，已能够适配组队
            BossBeAttackedRun task = new BossBeAttackedRun(role, spell.getDamage() +    extra, boss);
            DuplicateManager.addDupTask(getBindId(role), task);

            // 玩家装备耐久消耗
            equipService.synCutEquipDurability(role);

        }else {
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
                        // 施放持续技能，并记录任务
                        spellDurRecord(role, spell, dup, extra);
                    } else {
                        // 直接伤害技能
                        BossBeAttackedRun bossTask = new BossBeAttackedRun(role, spell.getDamage() + extra, boss);
                        DuplicateManager.addDupTask(getBindId(role), bossTask);
                    }
                } else {
                    sendFailedMsg(ContentType.DUPLICATE_SPELL_FAILED, channel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 施放续伤害技能伤害并记录task
     *
     * @param role
     * @param spell
     * @param dup
     * @param extra
     */
    private void spellDurRecord(Role role, Spell spell, Duplicate dup, int extra) {
        BossBeAttackedScheduleRun scheduleTask = new BossBeAttackedScheduleRun(role, spell, dup, extra);
        CustomExecutor executor = DuplicateManager.getExecutor(role.getRoleId());
        ScheduledFuture future = executor.scheduleAtFixedRate(scheduleTask, 0, 2, TimeUnit.SECONDS);

        // 记录任务，等待取消
        FutureMap.futureMap.put(scheduleTask.hashCode(), future);

        List<ScheduledFuture> scheduledFutures = LocalAttackCreepMap.getSpellToBossFutMap().get(role.getRoleId());
        if (scheduledFutures == null){
            scheduledFutures = new ArrayList<>();
        }
        scheduledFutures.add(future);
        LocalAttackCreepMap.getSpellToBossFutMap().put(role.getRoleId(), scheduledFutures);
    }

    /**
     * 加锁判断boss的状态
     *
     * @param dup
     * @return
     */
    private synchronized MsgDuplicateProto.ResponseDuplicate synGetBossInfo(Duplicate dup) {
        // 判断存活的boss，因为死亡的boss 会被移除 duplicate 的 bossList
        if (dup == null){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.DUPLICATE_BOSS_HAD_DEATH)
                    .build();
        }
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
     * 1.移除Boss的目标队列
     * 2.Boss 重新选取目标进行攻击
     * 3.消息返回
     *
     * @param request
     * @return
     */
    public void stopAttack(MsgDuplicateProto.RequestDuplicate request, Channel channel) {
        // 获取请求的基本信息
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        Duplicate nowDup = LocalAttackCreepMap.getCurDupMap().get(getBindId(role));

        // 将当前玩家移除 Boss的目标队列
        removeAttackedQueue(role);

        // 重新选取目标
        doBossAttacked(nowDup);

        // 消息返回
        sendCommonMsg(channel, ContentType.DUPLICATE_LEAVE_SUCCESS);
    }


    /**
     * 玩家离开当前副本，退出
     *
     * 1. 如果是个人挑战离开，那么将直接退出副本模式，副本失效
     * 2. 如果是队伍挑战，那么除非队伍中没有人，否则场景不退出
     *
     * @param request
     * @param channel
     * @return
     */
    public void leaveDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel){

        // 获取基本信息
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(getBindId(role));

        // 判断是否是队伍挑战
        Group group = GroupCache.getRoleGroupMap().get(role.getRoleId());
        if (group != null){
            // 挑战队伍还有其他人
            // 将玩家移除队伍，并重新选取目标进行攻击
            if (group.getMembers().size() > 1){
                removeAttackedQueue(role);

                sendCommonMsg(channel, ContentType.DUPLICATE_LEAVE_SUCCESS);

                // 重新选取目标
                doBossAttacked(dup);
                return;
            }
        }

        // 销毁副本
        destroyDupSource(role, dup);
    }



    /**
     * 获取副本线程池，开始执行 Boss的攻击任务
     *
     * @param dup   副本
     */
    public void doBossAttacked(Duplicate dup) {
        // 队列为空，直接销毁副本信息
        Queue<Long> queue = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (queue == null || queue.isEmpty()){
            destroyDupSource(null, dup);
            return;
        }
        // 当前挑战的 Boss
        Boss b = dup.getBosses().get(0);

        // 获取技能列表，并开始执行攻击玩家
        List<BossSpell> spells = b.getSpellList();
        spells.forEach(s -> {
            if (s.getSec() == 0){
                if (s.getRange() == 0){
                    // 普通攻击
                    BossDoAttackedRun task = new BossDoAttackedRun(dup, s);
                    CustomExecutor executor = DuplicateManager.getExecutor(dup.getRelatedId());
                    ScheduledFuture future = executor.scheduleAtFixedRate(task, 0, s.getCd(), TimeUnit.SECONDS);

                    // 记录任务
                    List<ScheduledFuture> dupAllFuture = LocalAttackCreepMap.getDupAllFutureMap().get(dup.getRelatedId());
                    if (dupAllFuture == null){
                        dupAllFuture = new ArrayList<>();
                    }
                    dupAllFuture.add(future);
                    LocalAttackCreepMap.getDupAllFutureMap().put(dup.getRelatedId(), dupAllFuture);
                }
            }
        });
    }


    /**
     * 用于创建临时副本信息，便于后续回收
     *
     * @param request
     * @return
     */
    private Duplicate createTmpDuplicate(MsgDuplicateProto.RequestDuplicate request, Role role) {
        long dupId = request.getDupId();

        // 绑定副本对象, 如果是组队模式，绑定的id为 groupId，否则为 roleId
        long bindId = getBindId(role);

        // 读取副本模板信息
        Duplicate data = LocalDuplicateMap.getDuplicateMap().get(dupId);

        // 构建临时副本对象，便于回收
        Duplicate tmp = new Duplicate();
        BeanUtils.copyProperties(data, tmp);

        // 设置临时资源
        tmp.setRelatedId(bindId);
        List<Boss> bosses = data.getBosses();
        List<Boss> tmpBosses = new ArrayList<>();
        bosses.forEach(b -> {
            Boss boss = new Boss();
            Boss dataBoss = LocalBossMap.getBossMap().get(b.getId());
            BeanUtils.copyProperties(dataBoss, boss);
            tmpBosses.add(boss);
        });
        tmp.setBosses(tmpBosses);

        LocalAttackCreepMap.getCurDupMap().put(bindId, tmp);

        // 绑定线程池
        DuplicateManager.bindDupExecutor(bindId);

        // 记录下挑战boss的时间点
        long now = System.currentTimeMillis();
        LocalAttackCreepMap.getDupTimeStampMap().put(bindId, now);

        return tmp;
    }


    /**
     * 用于记录Boss 的攻击目标队列
     *
     * @param role
     */
    private void recordRoleAttacked(Role role, Duplicate tmpDup){
        // 记录下进入Boss的次序
        // 后续在这里修改 组队的时候 Boss 的攻击判断
        Queue<Long> queue = new LinkedList<>();
        if (groupService.hasGroup(role)){
            List<Role> roleList = groupService.getGroupRoleList(GroupCache.getRoleGroupMap().get(role.getRoleId()));

            // 根据角色的职业类型进行排序
            roleList.sort((r1, r2) -> r1.getType().compareTo(r2.getType()));
            roleList.forEach( r -> queue.offer(r.getRoleId()));
        }else {
            queue.offer(role.getRoleId());
        }
        LocalAttackCreepMap.getBossAttackQueueMap().put(tmpDup.getRelatedId(), queue);
    }

    /**
     * 将当前玩家移除 Boss 的目标队列
     *
     * @param role
     */
    public void removeAttackedQueue(Role role){
        if (role == null){
            return;
        }
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(getBindId(role));
        // 移除目标队列
        Queue<Long> queue = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (queue != null){
            queue.remove(role.getRoleId());
        }
    }


    /**
     * 将副本中的持续任务全部取消
     *
     * @param dup
     */
    public void removeDupFutureList(Duplicate dup) {
        List<ScheduledFuture> futureList = LocalAttackCreepMap.getDupAllFutureMap().get(dup.getRelatedId());
        if (futureList != null && !futureList.isEmpty()){
            futureList.forEach(f -> f.cancel(true));
        }
        LocalAttackCreepMap.getDupAllFutureMap().remove(dup.getRelatedId());
    }

    /**
     * 获取挑战副本的唯一ID标识
     *
     * @param role   玩家
     * @return
     */
    public Long getBindId(Role role){
        long rId;
        if (groupService.hasGroup(role)){
            rId = GroupCache.getRoleGroupMap().get(role.getRoleId()).getId();
        }else {
            rId = role.getRoleId();
        }
        return rId;
    }


    /**
     * 获取玩家当前的副本信息
     *
     * @param role  玩家信息
     */
    public Duplicate getDuplicate(Role role){
        Group group = GroupCache.getRoleGroupMap().get(role.getRoleId());
        if (group == null){
            return LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());
        }
        return LocalAttackCreepMap.getCurDupMap().get(group.getId());
    }


    /**
     * 初始化 Boss 的攻击目标数据
     *
     * @param dup
     */
    public Role getFirstAimFromQueue(Duplicate dup){
        Queue<Long> queue = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (queue != null && !queue.isEmpty()){
            long aimId = queue.peek();
            return LocalUserMap.idRoleMap.get(aimId);
        }
        return null;
    }


    /**
     * 施放副本的资源
     *
     * 1.移除挑战队列
     * 2.移除挑战时间
     * 3.移除玩家-副本关联
     * 4.施放临时副本线程池
     * 5.施放对象
     *
     * @param role
     */
    public void destroyDupSource(Role role,  Duplicate dup){
        log.info("临时副本：" + dup.getRelatedId() + " 开始销毁！");
        // 删除副本信息
        long bindId = dup.getRelatedId();

        // 移除副本任务
        removeDupFutureList(dup);

        // 移除目标队列
        removeAttackedQueue(role);
        LocalAttackCreepMap.getBossAttackQueueMap().remove(bindId);

        // 移除挑战时间
        LocalAttackCreepMap.getDupTimeStampMap().remove(bindId);

        // 移除副本-玩家关联
        LocalAttackCreepMap.getCurDupMap().remove(bindId);

        // 最后释放暂用的副本线程池
        DuplicateManager.unBindDupExecutor(bindId);

        dup = null;
        log.info("临时副本销毁结束！");
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
     * 返回失败的消息
     *
     * @param content
     * @param channel
     */
    private void sendFailedMsg(String content, Channel channel) {
        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        channel.writeAndFlush(dupResp);
    }



    /**
     * 发送副本消息， 个人发送 / 挑战队伍发送
     *
     * @param role
     * @param dupResp
     */
    private void sendDuplicateMsg(Role role, MsgDuplicateProto.ResponseDuplicate dupResp) {
        if (groupService.hasGroup(role)){
            long groupId = GroupCache.getRoleGroupMap().get(role.getRoleId()).getId();
            ChannelGroup cg = ChannelCache.getGroupChannelMap().get(groupId);
            cg.writeAndFlush(dupResp);
        }else {
            Channel c = ChannelCache.getUserIdChannelMap().get(role.getUserId());
            c.writeAndFlush(dupResp);
        }
    }


    /**
     * 返回文本信息
     *
     * @param channel
     * @param msg
     */
    private void sendCommonMsg(Channel channel, String msg){
        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgDuplicateProto.RequestType.LEAVE)
                .setContent(msg)
                .build();
        channel.writeAndFlush(dupResp);
    }
}
