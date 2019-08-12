package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.RolePKDao;
import com.ljh.gamedemo.entity.PKRecord;
import com.ljh.gamedemo.entity.PKReward;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.local.LocalPKRewardMap;
import com.ljh.gamedemo.local.LocalSpellMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.RoleInvitePKCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgPKProto;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.run.user.UserDeclineMpRun;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
        RoleInvitePKCache.getPkInviteCache().put(dRole.getRoleId(), cRole.getRoleId());

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
        // 被攻击者信息
        Role role = LocalUserMap.getIdRoleMap().get(req.getRoleId());
        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);

        // 具体攻击操作
        doSpellPk(role, spell);
    }


    /**
     * 使用技能进行攻击操作
     *
     * @param role
     * @param spell
     */
    private void doSpellPk(Role role, Spell spell) {
        // 普通攻击技能，直接打伤害
        if (spell.getCost() == 0){
            doAttackRole(role, spell);
            return;
        }

        // 判断技能的类型
        doSpellRole(role, spell);
    }

    /**
     * 玩家受到普通攻击伤害
     *
     * @param role
     * @param spell
     */
    private void doAttackRole(Role role, Spell spell) {
        // 构建任务
        UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), spell.getDamage(), true);
        UserExecutorManager.addUserTask(role.getUserId(), task);
    }


    /**
     * 使用技能攻击敌方角色
     *
     * @param role
     * @param spell
     */
    private void doSpellRole(Role role, Spell spell) {
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
                    UserBeAttackedRun
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
            return null;
        }
        return combineFailed(ContentType.PK_SITE_NOT_IN_ARENA);
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
        if (record != null && record.getDefender() != dRoleId){
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
                .setResult(ResultCode.SUCCESS)
                .setContent(content)
                .setOwn(protoService.transToRole(cRole))
                .setOpponent(protoService.transToRole(dRole))
                .build();
        return pkResp;
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
        PKRecord pkRecord = RoleInvitePKCache.getPkRecordMap().get(role.getUserId());

        // 挑战者，被挑战者
        long cRoleId = pkRecord.getChallenger();
        long dRoleId = pkRecord.getDefender();

        Role winner = role.getRoleId() == cRoleId ? LocalUserMap.getIdRoleMap().get(cRoleId) : LocalUserMap.getIdRoleMap().get(dRoleId);
        Role loser = role.getRoleId() != cRoleId ? LocalUserMap.getIdRoleMap().get(cRoleId) : LocalUserMap.getIdRoleMap().get(dRoleId);

        // 发放奖励
        sendHonorReward(winner, loser, pkRecord);

        // 消息发送
        sendBothMsg(winner, loser, pkRecord);

        // 更新记录，删除本地的pk缓存
        updatePKRecord(winner, loser, pkRecord);
    }
}
