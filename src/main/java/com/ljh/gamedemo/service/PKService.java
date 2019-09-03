package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.RolePKDao;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.LocalPKRewardMap;
import com.ljh.gamedemo.local.LocalSpellMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.RoleAttrCache;
import com.ljh.gamedemo.local.cache.RoleInvitePKCache;
import com.ljh.gamedemo.local.cache.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgPKProto;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.run.user.UserBeAttackedScheduleRun;
import com.ljh.gamedemo.run.user.UserDeclineMpRun;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 具体的 PK 操作
 *
 *
 * @Author: Heiku
 * @Date: 2019/8/12
 */
@Slf4j
@Service
public class PKService {

    @Autowired
    private RolePKDao pkDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SpellService spellService;

    @Autowired
    private ProtoService protoService;

    private MsgUserInfoProto.ResponseUserInfo userResp;

    private MsgPKProto.ResponsePK pkResp;

    private MsgSpellProto.ResponseSpell spellResp;

    /**
     * 玩家进行pk挑战
     *
     * @param req
     * @param channel
     */
    public void pk(MsgPKProto.RequestPK req, Channel channel) {

        // 用户状态认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 判断玩家是否都在竞技场
        pkResp = pkSiteInterceptor(req.getUserId(), req.getRoleId());
        if (pkResp != null){
            channel.writeAndFlush(pkResp);
            return;
        }

        // 获取双方的信息
        Role cRole = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Role dRole = LocalUserMap.getIdRoleMap().get(req.getRoleId());

        // 存储发起挑战的信息
        RoleInvitePKCache.getPkInviteCache().put(cRole.getRoleId(), dRole.getRoleId());

        // 发起 pk 挑战邀请
        sendPKInvite(cRole, dRole);

        // 回复玩家消息
        responseMsg(channel, ContentType.PK_INVITE_SEND_SUCCESS);
    }


    /**
     * 接收方接受挑战，准备进行 pk 对决
     *
     * @param req
     * @param channel
     */
    public void acceptChallenge(MsgPKProto.RequestPK req, Channel channel) {

        // 用户状态认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }
        long nowRoleId = LocalUserMap.getUserRoleMap().get(req.getUserId()).getRoleId();

        // 获取接收方的 挑战id
        long cRoleId = req.getRoleId();
        Long dRoleId = RoleInvitePKCache.getPkInviteCache().getIfPresent(cRoleId);

        // 获取双方的角色状态
        Role cRole = LocalUserMap.getIdRoleMap().get(cRoleId);
        Role dRole = LocalUserMap.getIdRoleMap().get(dRoleId);


        // 接受pk挑战，进入pk挑战模式
        if (dRoleId != null && nowRoleId == dRoleId){
            enterPKMode(cRole, dRole);
        }
        
        responseMsg(channel, ContentType.PK_INVITE_ACCEPT_SUCCESS);
    }


    /**
     * 玩家开始pk 攻击，使用技能进行战斗
     *
     * @param req
     * @param channel
     */
    public void spellRole(MsgPKProto.RequestPK req, Channel channel) {
        // 用户状态认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 技能状态认证
        spellResp = spellService.spellStateInterceptor(req.getSpellId());
        if (spellResp != null){
            channel.writeAndFlush(spellResp);
            return;
        }

        // pk 状态认证
        pkResp = pkStateInterceptor(req.getUserId(), req.getRoleId());
        if (pkResp != null){
            channel.writeAndFlush(pkResp);
            return;
        }
        int spellId = req.getSpellId();
        Role fromRole = LocalUserMap.getUserRoleMap().get(req.getUserId());


        // 被攻击者信息
        Role toRole = LocalUserMap.getIdRoleMap().get(req.getRoleId());
        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);

        // 具体攻击操作
        doSpellPk(fromRole, toRole, spell);
    }


    /**
     * pk 玩家退出战斗
     *
     * @param req
     * @param channel
     */
    public void escape(MsgPKProto.RequestPK req, Channel channel) {
        // 用户状态认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // pk 状态认证
        // 只有在 pk 状态下才能进行退出
        pkResp = pkStateInterceptor(req.getUserId(), req.getRoleId());
        if (pkResp != null){
            channel.writeAndFlush(pkResp);
            return;
        }

        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 如果有扣血任务，则取消
        ScheduledFuture future = RoleInvitePKCache.getPkFutureMap().get(role.getRoleId());
        if (future != null){
            future.cancel(true);
            RoleInvitePKCache.getPkFutureMap().remove(role.getRoleId());
        }

        // 判断本场 PK 的结果，
        PKRecord record = RoleInvitePKCache.getPkRecordMap().get(role.getRoleId());
        long another = record.getChallenger().longValue() == role.getRoleId() ? record.getChallenger() : record.getDefender();

        // 放弃方战败
        record.setWinner(another);
        record.setLoser(role.getRoleId());

        Role winner = LocalUserMap.getIdRoleMap().get(another);

        // 完成 pk 操作
        finishPK(winner, role, record);

        responseMsg(channel, ContentType.PK_ESCAPE_SUCCESS);
    }

    /**
     * 使用技能进行攻击操作
     *
     * @param fromRole
     * @param toRole
     * @param spell
     */
    private void doSpellPk(Role fromRole, Role toRole, Spell spell) {
        // 普通攻击技能，直接打伤害
        if (spell.getCost() == 0){
            doAttackRole(fromRole, toRole, spell);
            return;
        }

        // 判断技能的类型
        doSpellRole(fromRole, toRole, spell);

        // 通知攻击成功
        Channel channel = ChannelCache.getUserIdChannelMap().get(fromRole.getUserId());
        responseMsg(channel, ContentType.ATTACK_SPELL_SUCCESS);
    }

    /**
     * 玩家受到普通攻击伤害
     *
     * @param role
     * @param spell
     */
    private void doAttackRole(Role fromRole, Role role, Spell spell) {
        // 获取普攻的额外伤害
        int extra = RoleAttrCache.getRoleAttrMap().get(fromRole.getRoleId()).getDamage();

        // 构建任务
        UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), spell.getDamage() + extra, true);
        UserExecutorManager.addUserTask(role.getUserId(), task);
    }


    /**
     * 使用技能攻击敌方角色
     *
     * @param toRole
     * @param spell
     */
    private void doSpellRole(Role fromRole, Role toRole, Spell spell) {
        // 先进行扣蓝操作
        UserDeclineMpRun mpTask = new UserDeclineMpRun(fromRole.getRoleId(), spell);
        Future<Boolean> mpFuture = UserExecutorManager.addUserCallableTask(fromRole.getUserId(), mpTask);

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

                // 获取技能的额外加成
                int extra = RoleAttrCache.getRoleAttrMap().get(fromRole.getRoleId()).getSp();
                if (spell.getSec() > 0) {

                    DurationAttack da = new DurationAttack(spell.getDamage(), spell.getSec());
                    // 任务处理
                    UserBeAttackedScheduleRun task = new UserBeAttackedScheduleRun(toRole, da, extra, true);
                    CustomExecutor executor = UserExecutorManager.getUserExecutor(toRole.getUserId());
                    ScheduledFuture future = executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);

                    // 任务记录
                    FutureMap.getFutureMap().put(task.hashCode(), future);
                    RoleInvitePKCache.getPkFutureMap().put(toRole.getRoleId(), future);

                }else {
                    UserBeAttackedRun task = new UserBeAttackedRun(toRole.getUserId(), spell.getDamage() + extra, true);
                    UserExecutorManager.addUserTask(toRole.getUserId(), task);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 进入pk竞技场
     *
     * 1.初始化奖励 pk 奖励信息
     * 2.保存双方的 pk 记录
     * 3.通知双方已经进入 pk 竞技场的消息通知
     *
     *
     * @param cRole
     * @param dRole
     */
    private void enterPKMode(Role cRole, Role dRole) {
        // 获取奖励等级
        int level = calculateRewardLevel(cRole.getLevel(), dRole.getLevel());
        // 获取奖励信息
        PKReward reward = LocalPKRewardMap.getPkLevelRewardMap().get(level);

        // 存储pk记录 (cache + db)
        savePKRecord(cRole, dRole, reward);

        // 向双方发送可以开始pk的消息
        sendBothSidesMsg(cRole, dRole);
    }



    /**
     * 存储 pk 记录
     *
     * @param cRole
     * @param dRole
     * @param reward
     */
    private void savePKRecord(Role cRole, Role dRole, PKReward reward) {

        // 组装record
        PKRecord record = new PKRecord();
        record.setChallenger(cRole.getRoleId());
        record.setDefender(dRole.getRoleId());
        record.setWinHonor(reward.getWin());
        record.setLoseHonor(reward.getLose());
        record.setCreateTime(new Date());

        // 持久 pk 记录
        int n = pkDao.insertPKRecord(record);
        log.info("插入pk挑战记录，id：" + record.getId() + " 影响数据行：" + n);

        // 本地缓存存储
        RoleInvitePKCache.getPkRecordMap().put(cRole.getRoleId(), record);
        RoleInvitePKCache.getPkRecordMap().put(dRole.getRoleId(), record);
    }


    /**
     * 向 pk 双方发送可以进行pk的消息
     *
     * @param cRole
     * @param dRole
     */
    private void sendBothSidesMsg(Role cRole, Role dRole) {
        Channel cCh = ChannelCache.getUserIdChannelMap().get(cRole.getUserId());
        Channel dCh = ChannelCache.getUserIdChannelMap().get(dRole.getUserId());

        // 消息返回
        responseMsg(cCh, ContentType.PK_ENTER_PK_STATE_SUCCESS);
        responseMsg(dCh, ContentType.PK_ENTER_PK_STATE_SUCCESS);
    }


    /**
     * 根据双方角色的level 计算出 玩家pk 的奖励等级
     *
     * @param cLevel
     * @param dLevel
     * @return
     */
    private int calculateRewardLevel(int cLevel, int dLevel) {
        // 这里粗略地计算等级值
        return (cLevel + dLevel) % 3 + 1;
    }

    /**
     * 向挑战方发送挑战邀请
     *
     * @param cRole
     * @param dRole
     */
    private void sendPKInvite(Role cRole, Role dRole) {
        // 找到接收方的 channel
        Channel channel = ChannelCache.getUserIdChannelMap().get(dRole.getUserId());
        String msg = String.format(ContentType.PK_SEND_MSG, cRole.getRoleId(), cRole.getName());

        // 构造resp
        pkResp = combineSuccess(msg, cRole, dRole);

        // 发送挑战信息
        channel.writeAndFlush(pkResp);
    }


    /**
     * 直接返回消息
     *
     * @param channel
     * @param msg
     */
    private void responseMsg(Channel channel, String msg){
        pkResp = MsgPKProto.ResponsePK.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgPKProto.RequestType.AC)
                .setContent(msg)
                .build();
        channel.writeAndFlush(pkResp);
    }


    /**
     * pk结束，构建pk信息
     *
     * 1.判断获胜方喝战败方
     * 2.更新pk记录
     * 3.删除缓存
     * 4.发送奖励荣誉值
     * 5.消息发送（双方）
     *
     * @param role
     */
    public void generatePkRecord(Role role) {
        // 获取之前的记录信息
        PKRecord pkRecord = RoleInvitePKCache.getPkRecordMap().get(role.getRoleId());

        // 挑战者，被挑战者
        long winnerId = role.getRoleId().longValue() == pkRecord.getChallenger() ? pkRecord.getDefender() : pkRecord.getChallenger();
        Role winner = LocalUserMap.getIdRoleMap().get(winnerId);

        // 设置胜者ID
        pkRecord.setWinner(winner.getRoleId());
        pkRecord.setLoser(role.getRoleId());

        finishPK(winner, role, pkRecord);
    }


    /**
     * pk 操作完成
     *
     * 1.奖励发放
     * 2.消息发送
     * 3.更新db记录
     *
     * @param winner
     * @param loser
     * @param pkRecord
     */
    public void finishPK(Role winner, Role loser, PKRecord pkRecord){
        // 发放奖励
        sendHonorReward(winner, loser, pkRecord);

        // 消息发送
        sendBothMsg(winner, loser, pkRecord);

        // 更新记录，删除本地的pk缓存
        updatePKRecord(winner, loser, pkRecord);
    }


    /**
     * 在用户线程池中更新玩家的荣誉值信息
     *
     * @param winner
     * @param loser
     * @param pkRecord
     */
    private void sendHonorReward(Role winner, Role loser, PKRecord pkRecord) {
        winner.setHonor(winner.getHonor() + pkRecord.getWinHonor());
        loser.setHonor(loser.getHonor() + pkRecord.getLoseHonor());

        // 更新双方的荣誉值信息
        roleService.updateRoleInfo(winner);
        roleService.updateRoleInfo(winner);
    }


    /**
     * 向pk 的双方发送结果信息
     *
     * @param winner
     * @param loser
     * @param pkRecord
     */
    private void sendBothMsg(Role winner, Role loser, PKRecord pkRecord) {

        // 获得双方的通信channel
        Channel winCh = ChannelCache.getUserIdChannelMap().get(winner.getUserId());
        Channel loseCh = ChannelCache.getUserIdChannelMap().get(loser.getUserId());

        pkResp = combinePkResult(pkRecord, ContentType.PK_END);
        winCh.writeAndFlush(pkResp);
        loseCh.writeAndFlush(pkResp);
    }


    /**
     * 更新Db 中的战斗记录
     * 删除本地 pk 缓存
     *
     * @param winner
     * @param loser
     * @param pkRecord
     */
    private void updatePKRecord(Role winner, Role loser, PKRecord pkRecord) {
        pkRecord.setEndTime(new Date());
        int n = pkDao.updateRecord(pkRecord);
        log.info("update PK Record: affect rows: " + n);

        removePkRecord(winner, loser, pkRecord);
    }


    /**
     * 移除当前的 PK 信息
     *
     * @param winner
     * @param loser
     */
    private void removePkRecord(Role winner, Role loser, PKRecord pkRecord) {
        // 移除 PK 信息
        RoleInvitePKCache.getPkRecordMap().remove(winner.getRoleId());
        RoleInvitePKCache.getPkRecordMap().remove(loser.getRoleId());

        // 移除邀请信息
        RoleInvitePKCache.getPkInviteCache().invalidate(pkRecord.getChallenger());
    }


    /**
     * 返回 pk 战斗结果的通知消息
     *
     * @param msg
     * @return
     */
    private MsgPKProto.ResponsePK combinePkResult(PKRecord record, String msg) {
        pkResp = MsgPKProto.ResponsePK.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgPKProto.RequestType.RESULT)
                .setContent(msg)
                .setRecord(protoService.transToPkRecord(record))
                .build();
        return pkResp;
    }


    /**
     * pk结束，一方阵亡
     */
    public void pkEnd(Role role){
        generatePkRecord(role);
        roleService.reliveRole(role);
    }


    /**
     * 规定 PK 战斗只能在竞技场上进行，判断玩家双方是否都在竞技场上
     *
     * @param challenge
     * @param defender
     * @return
     */
    private MsgPKProto.ResponsePK pkSiteInterceptor(long challenge, long defender){
        // 竞技场id
        int arena = 7;

        Role cRole = LocalUserMap.getUserRoleMap().get(challenge);
        Role dRole = LocalUserMap.getIdRoleMap().get(defender);

        //判断是否都在竞技场位置上
        if (cRole.getSiteId() != arena || dRole.getSiteId() != arena){
            return combineFailed(ContentType.PK_SITE_NOT_IN_ARENA);
        }
        return null;
    }


    /**
     * 判断玩家是否在 pk 状态中
     *
     * @param userId
     * @return
     */
    private MsgPKProto.ResponsePK pkStateInterceptor(long userId, long dRoleId){
        // 获取玩家状态
        Role role = LocalUserMap.getUserRoleMap().get(userId);

        // 获取 pk 信息
        PKRecord record = RoleInvitePKCache.getPkRecordMap().get(role.getRoleId());
        if (record != null && (record.getDefender() != dRoleId || record.getChallenger() != dRoleId)){
            return null;
        }
        return combineFailed(ContentType.PK_STATE_WRONG);
    }


    /**
     * pk 失构造败消息返回
     *
     * @param content
     */
    private MsgPKProto.ResponsePK combineFailed(String content){
        pkResp = MsgPKProto.ResponsePK.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        return pkResp;
    }

    /**
     * pk 构造成功消息返回
     */
    private MsgPKProto.ResponsePK combineSuccess(String content, Role cRole, Role dRole){
        pkResp = MsgPKProto.ResponsePK.newBuilder()
                .setType(MsgPKProto.RequestType.AC)
                .setResult(ResultCode.SUCCESS)
                .setContent(content)
                .setOwn(protoService.transToRole(cRole))
                .setOpponent(protoService.transToRole(dRole))
                .build();
        return pkResp;
    }


}
