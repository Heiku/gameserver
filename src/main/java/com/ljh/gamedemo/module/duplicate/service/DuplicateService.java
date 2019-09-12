package com.ljh.gamedemo.module.duplicate.service;

import com.ljh.gamedemo.module.creep.local.LocalAttackCreepMap;
import com.ljh.gamedemo.module.duplicate.local.LocalBossMap;
import com.ljh.gamedemo.module.duplicate.local.LocalDuplicateMap;
import com.ljh.gamedemo.module.spell.local.LocalSpellMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.role.tmp.RoleDeBuff;
import com.ljh.gamedemo.module.group.cache.GroupCache;
import com.ljh.gamedemo.module.role.cache.RoleBuffCache;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.duplicate.bean.Boss;
import com.ljh.gamedemo.module.duplicate.bean.BossSpell;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.equip.service.EquipService;
import com.ljh.gamedemo.module.group.bean.Group;
import com.ljh.gamedemo.module.group.service.GroupService;
import com.ljh.gamedemo.module.spell.bean.Partner;
import com.ljh.gamedemo.module.spell.bean.Spell;
import com.ljh.gamedemo.module.spell.service.PartnerService;
import com.ljh.gamedemo.module.spell.service.SpellService;
import com.ljh.gamedemo.proto.protoc.DuplicateProto;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.manager.DuplicateManager;
import com.ljh.gamedemo.run.manager.UserExecutorManager;
import com.ljh.gamedemo.run.dup.BossBeAttackedRun;
import com.ljh.gamedemo.run.dup.BossBeAttackedScheduleRun;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserDeclineMpRun;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.role.service.RoleAttrService;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.ljh.gamedemo.common.SpellSchoolType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/30
 *
 * 副本相关操作
 */

@Service
@Slf4j
public class DuplicateService {

    /**
     * 玩家额外属性服务
     */
    @Autowired
    private RoleAttrService attrService;

    /**
     * 玩家服务
     */
    @Autowired
    private RoleService roleService;

    /**
     * 组队服务
     */
    @Autowired
    private GroupService groupService;

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
     * Boss服务
     */
    @Autowired
    private BossService bossService;

    /**
     * 召唤伙伴服务
     */
    @Autowired
    private PartnerService partnerService;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 用户协议返回
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    /**
     * 技能协议返回
     */
    private MsgSpellProto.ResponseSpell spellResp;

    /**
     * 副本协议返回
     */
    private MsgDuplicateProto.ResponseDuplicate dupResp;

    /**
     * 获取当前的所有副本信息
     *
     * @param request   请求
     */
    public void getDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel){

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
     * @param request   请求
     */
    public void enterDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel) {

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
     * @param request       请求
     * @param channel       channel
     */
    public void spellBoss(MsgDuplicateProto.RequestDuplicate request, Channel channel){
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

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

        // 进行 deBuff 判断
        dupResp = hasDeBuff(role);
        if (dupResp != null){
            channel.writeAndFlush(dupResp);
            return;
        }

        // 具体攻击 Boss 操作
        doSpellAttack(role, spell, dup, channel);
    }


    /**
     * 判断玩家是否被施放 DeBuff技能 （例如：眩晕）
     *
     * @param role  玩家信息
     */
    private MsgDuplicateProto.ResponseDuplicate hasDeBuff(Role role) {
        RoleDeBuff deBuff = RoleBuffCache.getRoleDeBuffMap().get(role.getRoleId());
        if (deBuff == null){
            return null;
        }
        long ts = deBuff.getTs();
        long now = System.currentTimeMillis();

        // 超过 deBuff 的持续时间，清楚 deBuff 缓存信息
        if (now - ts >= deBuff.getSec()){
            RoleBuffCache.getRoleDeBuffMap().remove(role.getRoleId());
            return null;
        }

        // 获取时间差
        int t = Long.valueOf((now - ts) / 1000).intValue();
        dupResp = combineFailMsg(String.format(ContentType.DUPLICATE_DEBUFF_FAILED, t));

        return dupResp;
    }


    /**
     * 玩家技能对 Boss 造成伤害
     *
     * @param role      玩家信息
     * @param spell     技能信息
     * @param channel   channel
     */
    private void doSpellAttack(Role role, Spell spell, Duplicate dup, Channel channel) {
        int extra = attrService.getExtraDamage(role, spell);

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
            // 扣蓝，技能cd判断
            UserDeclineMpRun mpTask = new UserDeclineMpRun(role, spell);
            Future<Boolean> mpFuture = UserExecutorManager.addUserCallableTask(role.getUserId(), mpTask);

            // 异步转同步，等待扣蓝任务完成
            try {
                if (mpFuture == null){
                    return;
                }
                mpFuture.sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 判断是否扣蓝成功
            try {
                // 只有当用户扣蓝成功，再继续进行
                if (mpFuture.get()) {
                    switch (spell.getSchool()){

                        // 普攻或直接技能伤害
                        case COMMON: case DIRECT:
                            // 直接伤害技能
                            BossBeAttackedRun bossTask = new BossBeAttackedRun(role, spell.getDamage() + extra, boss);
                            DuplicateManager.addDupTask(getBindId(role), bossTask);
                            break;

                         // 持续掉血技能
                        case DURATION:
                            // 施放持续技能，并记录任务
                            spellDurRecord(role, spell, dup, extra);
                            break;

                        // 治疗技能
                        case HEAL:
                            heal(spell, dup, extra);
                            break;

                        // 召唤技能
                        case PARTNER:
                            callPartner(role, spell, dup);
                            break;
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
     * 玩家使用治疗技能
     *
     * 1.治疗
     * 2.发送治疗消息通知
     *
     * @param spell     技能信息
     * @param dup       副本信息
     * @param extra     额外治疗量
     */
    private void heal(Spell spell, Duplicate dup, int extra) {
        // 获取挑战队列
        Deque<Long> deque = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());

        // 计算治疗量，更新玩家血量
        int heal = spell.getDamage() + extra;
        roleService.healRole(deque, heal, spell.getRange());
    }

    /**
     * 玩家施放召唤伙伴技能
     *
     * @param role      玩家信息
     * @param spell     技能信息
     * @param dup       副本信息
     */
    private void callPartner(Role role, Spell spell, Duplicate dup) {
        // 构建伙伴实体
        Partner p = spellService.doSpellCallPartner(role, spell);

        // 添加到副本挑战队列中，伙伴优先加到队首，吸引Boss的攻击伤害
        partnerService.addPartnerToDup(dup, p);

        // 开启伙伴的自动攻击
        partnerService.attackBoss(p, dup);
    }


    /**
     * 施放续伤害技能伤害并记录task
     *
     * @param role      玩家信息
     * @param spell     技能信息
     * @param dup       副本信息
     * @param extra     额外伤害数值
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
     * @param dup   副本信息
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
     * @param request   请求
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
     * @param request   请求
     * @param channel   channel
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
        Deque<Long> deque = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (deque == null || deque.isEmpty()){
            destroyDupSource(null, dup);
            return;
        }
        // 当前挑战的 Boss
        Boss b = dup.getBosses().get(0);

        // 获取技能列表，并开始执行攻击玩家
        List<BossSpell> spells = b.getSpellList();
        spells.forEach(s -> {
            switch (s.getSchool()){
                // 普攻攻击
                case COMMON:
                    bossService.bossCommonAttack(dup, s);
                    break;

                 // AOE攻击
                case AOE:
                    bossService.bossAOEAttack(dup, s);
                    break;

                // 眩晕攻击
                case DIZZINESS:
                    bossService.bossDizzinessAttack(dup, s);
                    break;

                // 中毒攻击
                case DURATION:
                    bossService.bossDurationAttack(dup, s);
                    break;
            }
        });
    }


    /**
     * 用于创建临时副本信息，便于后续回收
     *
     * @param request   请求
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
     * @param role      玩家信息
     * @param tmpDup    副本信息
     */
    private void recordRoleAttacked(Role role, Duplicate tmpDup){
        // 记录下进入Boss的次序
        // 后续在这里修改 组队的时候 Boss 的攻击判断
        Deque<Long> deque = new LinkedList<>();
        if (groupService.hasGroup(role)){
            List<Role> roleList = groupService.getGroupRoleList(GroupCache.getRoleGroupMap().get(role.getRoleId()));

            // 根据角色的职业类型进行排序
            roleList.sort((r1, r2) -> r1.getType().compareTo(r2.getType()));
            roleList.forEach( r -> deque.offer(r.getRoleId()));
        }else {
            deque.offer(role.getRoleId());
        }
        LocalAttackCreepMap.getBossAttackQueueMap().put(tmpDup.getRelatedId(), deque);
    }


    /**
     * 将当前玩家移除 Boss 的目标队列
     *
     * @param role  玩家信息
     */
    public void removeAttackedQueue(Role role){
        if (role == null){
            return;
        }
        Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(getBindId(role));
        // 移除目标队列
        Deque<Long> deque = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (deque != null){
            deque.remove(role.getRoleId());
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
     * @param dup   副本信息
     */
    public Long getFirstAimFromQueue(Duplicate dup){
        Deque<Long> deque = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (deque != null && !deque.isEmpty()){
            return deque.peek();
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
     * @param role  玩家信息
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
     * @param request   请求
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
     * @param content       消息
     * @param channel       channel
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
     * @param role      玩家信息
     * @param dupResp   返回
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
     * @param channel       channel
     * @param msg           消息
     */
    public void sendCommonMsg(Channel channel, String msg){
        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgDuplicateProto.RequestType.LEAVE)
                .setContent(msg)
                .build();
        channel.writeAndFlush(dupResp);
    }


    /**
     * 饭返回失败消息
     *
     * @param msg   消息
     */
    private MsgDuplicateProto.ResponseDuplicate combineFailMsg(String msg){
        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        return dupResp;
    }


    /**
     * 返回副本挑战失败的消息
     *
     * @param channel   channel
     * @param content   消息
     */
    public void sendDuplicateFailed(Channel channel, String content){
        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        channel.writeAndFlush(dupResp);
    }


    /**
     * 返回副本挑战失败的队伍消息
     *
     * @param cg        channelGroup
     * @param content   消息
     */
    public void sendDuplicateFailed(ChannelGroup cg, String content){
        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        cg.writeAndFlush(dupResp);
    }
}
