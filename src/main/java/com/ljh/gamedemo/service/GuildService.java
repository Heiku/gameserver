package com.ljh.gamedemo.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.*;
import com.ljh.gamedemo.dao.GuildApplyDao;
import com.ljh.gamedemo.dao.GuildDao;
import com.ljh.gamedemo.dao.GuildGoodsDao;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.*;
import com.ljh.gamedemo.local.cache.GuildCache;
import com.ljh.gamedemo.local.cache.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgGuildProto;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.ljh.gamedemo.common.GuildType.*;

/**
 * 公会具体操作
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */

@Slf4j
@Service
public class GuildService {

    /**
     * guildDao
     */
    @Autowired
    private GuildDao guildDao;

    /**
     * guildApplyDao
     */
    @Autowired
    private GuildApplyDao guildApplyDao;

    /**
     * guildGoodsDao
     */
    @Autowired
    private GuildGoodsDao guildGoodsDao;

    /**
     * 物品服务
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 邮件服务
     */
    @Autowired
    private EmailService emailService;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 公会返回
     */
    private MsgGuildProto.ResponseGuild guildResp;



    /**
     * 获取当前玩家所在的公会信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void guild(MsgGuildProto.RequestGuild req, Channel channel) {

        // 获取当前的公会信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Guild guild = getCurGuild(role.getRoleId());
        if (guild == null) {
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        List<GuildGoodsStore> storeList = GuildCache.getGuildStoreMap().get(guild.getId());
        if (storeList == null){
            storeList = Lists.newArrayList();
        }
        List<EmailGoods> emailGoods = Lists.newArrayList();
        storeList.forEach(s -> {
            EmailGoods ed = new EmailGoods();
            ed.setGid(s.getGoodsId());
            ed.setNum(s.getNum());

            emailGoods.add(ed);
        });

        // 组成消息，准备返回
        guildResp = combineResp(Lists.newArrayList(guild), null, emailGoods, MsgGuildProto.RequestType.GUILD);
        channel.writeAndFlush(guildResp);
    }


    /**
     * 获取所有的公会信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void guildAll(MsgGuildProto.RequestGuild req, Channel channel) {
        // 获取所有的公会信息
        List<Guild> guilds = getAllGuild();

        // 消息返回
        guildResp = combineResp(guilds, null, null, MsgGuildProto.RequestType.GUILD_ALL);
        channel.writeAndFlush(guildResp);
    }


    /**
     * 建立公会 （建立公会的荣誉值不得低于 200）
     *
     *
     * @param req       请求
     * @param channel   channel
     */
    public void establish(MsgGuildProto.RequestGuild req, Channel channel) {
        // 判断当前是否已经有公会信息了
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Guild guild = getCurGuild(role.getRoleId());
        if (guild != null){
            sendFailedMsg(channel, ContentType.GUILD_ESTABLISH_HAS_IN);
            return;
        }

        // 荣誉值判断
        if (role.getHonor() < ESTABLISH_HONOR){
            sendFailedMsg(channel, String.format(ContentType.GUILD_ESTABLISH_HONOR_NOT_ENOUGH, ESTABLISH_HONOR));
            return;
        }

        // 创建公会信息
        doEstablishGuild(role, req.getGuildName(), req.getBulletin());

        // 发送公会消息
        sendCommonMsg(channel, ContentType.GUILD_ESTABLISH_SUCCESS);
    }


    /**
     * 申请加入公会
     *
     * @param req        请求
     * @param channel    channel
     */
    public void applyGuild(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 判断玩家是否已经在公户中
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild != null){
            sendFailedMsg(channel, ContentType.GUILD_APPLY_HAS_IN);
            return;
        }

        // 判断申请的公会是否存在
        guild = GuildCache.getIdGuildMap().get(req.getGuildId());
        if (guild == null){
            sendFailedMsg(channel, ContentType.GUILD_APPLY_NOT_FOUND);
            return;
        }

        // 判断是否已经重复申请该公会
        List<GuildApply> applyList = Optional.ofNullable(GuildCache.getRoleGuildApplyMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        Optional<GuildApply> result = applyList.stream().filter(ga -> ga.getGuildId() == req.getGuildId()).findFirst();
        if (result.isPresent()){
            sendFailedMsg(channel, ContentType.GUILD_APPLY_HAS_APPLY);
            return;
        }

        // 进行申请操作
        doGuildApply(role, guild);

        sendCommonMsg(channel, ContentType.GUILD_APPLY_SUCCESS);
    }


    /**
     * 公会管理员获取所有的申请信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void applyAll(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取当前的公会信息
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
           sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
           return;
        }

        // 获取公会的成员信息
        Member member = GuildCache.getRoleMemberMap().get(role.getRoleId());
        if (member.getPosition() < MemberType.ELDER.getCode()){
            sendFailedMsg(channel, ContentType.GUILD_NOT_PERMISSION);
            return;
        }

        // 获取所有的申请信息
        List<GuildApply> applyList = GuildCache.getGuildApplyMap().get(guild.getId());

        guildResp = combineResp(null, applyList, null, MsgGuildProto.RequestType.APPLY_ALL);
        channel.writeAndFlush(guildResp);
    }


    /**
     * 对公会申请进行审批
     *
     * @param req       请求
     * @param channel   channel
     */
    public void approval(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取当前的公会信息
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        // 判断是否超出公会的最大人数
        if (guild.getNum() >= guild.getMaxNum()){
            sendFailedMsg(channel, ContentType.GUILD_OUT_OF_MAX_NUM);
            return;
        }

        // 获取基本信息
        long applyId = req.getApplyId();
        int approval = req.getApproval();

        // 获取公会的成员信息
        Member member = GuildCache.getRoleMemberMap().get(role.getRoleId());
        if (member.getPosition() < MemberType.ELDER.getCode()){
            sendFailedMsg(channel, ContentType.GUILD_NOT_PERMISSION);
            return;
        }

        // 进行审批
        doApproval(role, guild, applyId, approval);

        sendCommonMsg(channel, ContentType.GUILD_APPROVAL_SUCCESS);
    }


    /**
     * 修改公会公告信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void modifyAnn(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取当前的公会信息
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        // 获取修改信息
        String ann = req.getBulletin();

        // 获取公会的成员信息
        Member member = GuildCache.getRoleMemberMap().get(role.getRoleId());
        if (member.getPosition() < 4){
            sendFailedMsg(channel, ContentType.GUILD_NOT_PERMISSION);
            return;
        }

        // 更新记录，并保存
        guild.setBulletin(ann);
        int n = guildDao.updateGuild(guild);
        log.info("update guild, affected rows: " + n);

        GuildCache.getIdGuildMap().put(guild.getId(), guild);

        // 消息返回
        sendCommonMsg(channel, ContentType.GUILD_CHANGE_INFO_SUCCESS);
    }


    /**
     * 赋予公会成员权限
     *
     * @param req       请求
     * @param channel   channel
     */
    public void give(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取当前的公会信息
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        long roleId = req.getRoleId();
        int position = req.getPosition();

        // 公会成员判断
        Member member = GuildCache.getRoleMemberMap().get(roleId);
        if (member == null || member.getGid().longValue() != guild.getId()){
            sendFailedMsg(channel, ContentType.GUILD_GIVE_FAILED_WRONG_INFO);
            return;
        }

        // 判断权限信息
        Member own = GuildCache.getRoleMemberMap().get(role.getRoleId());
        if (own.getPosition() < 2 || own.getPosition() <= position){
            sendFailedMsg(channel, ContentType.GUILD_GIVE_FAILED_WRONG_PERMISSION);
            return;
        }

        // 更改权限
        doChangePosition(member, guild, position);

        // 消息发送
        sendCommonMsg(channel, ContentType.GUILD_GIVE_SUCCESS);
    }


    /**
     * 玩家捐赠物品到公会仓库
     *
     * @param req       请求
     * @param channel   channel
     */
    public synchronized void donate(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取当前的公会信息
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        long goodsId = req.getGoodsId();
        int num = req.getNum();

        // 判断是否包含这些物品
        boolean b = goodsService.containGoods(role, goodsId, num);
        if (b){
            // 移除玩家物品信息
            goodsService.removeRoleGoods(role.getRoleId(), goodsId, num);

            // 仓库物品变更记录
            if (!goodsStoreChange(role, guild, goodsId, num, true)){
                return;
            }

            // 物品改动记录
            goodsRecord(role, guild, goodsId, num, true);

            // 增加玩家贡献值
            addRoleGuildContribution(role, goodsId, num);
            
            // 公会消息通知
            sendGuildChannel(guild, role, goodsId, num);
        }else {
            sendFailedMsg(channel, ContentType.GUILD_DONATE_FAILED_WITHOUT_GOODS);
            return;
        }
        sendCommonMsg(channel, ContentType.GUILD_DONATE_SUCCESS);
    }


    /**
     * 发送公会消息
     *
     * @param guild     公会信息
     * @param role      玩家信息
     * @param goodsId   物品id
     * @param num       数量
     */
    private void sendGuildChannel(Guild guild, Role role, long goodsId, int num) {
        ChannelGroup cg = ChannelCache.getGuildChannelMap().get(guild.getId());
        String name = LocalGoodsMap.getIdGoodsMap().get(goodsId).getType().intValue() == CommodityType.ITEM.getCode() ?
                LocalItemsMap.getIdItemsMap().get(goodsId).getName() :
                LocalEquipMap.getIdEquipMap().get(goodsId).getName();
        String msg = String.format(ContentType.GUILD_DONATE_MSG, role.getName(), name, num);
        sendCommonMsg(cg, msg);
    }


    /**
     * 更新玩家的贡献值
     *
     * @param role      玩家信息
     * @param goodsId   物品id
     * @param num       物品数量
     */
    private void addRoleGuildContribution(Role role, long goodsId, int num) {
        // 获取成员信息
        Member member = GuildCache.getRoleMemberMap().get(role.getRoleId());
        Goods goods = LocalGoodsMap.getIdGoodsMap().get(goodsId);
        int con;
        if (goods.getType().intValue() == CommodityType.ITEM.getCode()){
            con = LocalItemsMap.getIdItemsMap().get(goodsId).getMinTrans();
        }else {
            con = LocalEquipMap.getIdEquipMap().get(goodsId).getMinTrans();
        }
        member.setTodayCon(member.getTodayCon() + con * num);
        member.setAllCon(member.getAllCon() + con);

        // 更新数据库
        int n = guildDao.updateMemberByRoleId(member);
        log.info("update guild_member, affected rows: " + n);

        // 更新缓存
        GuildCache.getRoleMemberMap().put(member.getRoleId(), member);
    }


    /**
     * 玩家取出公会物品
     *
     * @param req           玩家请求
     * @param channel       channel
     */
    public synchronized void takeOut(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取当前的公会信息
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        // 获取物品信息
        int num = req.getNum();
        long goodsId = req.getGoodsId();

        // 判断是否取出成功
        boolean b = goodsStoreChange(role, guild, goodsId, num, false);
        if (!b){
            sendFailedMsg(channel, ContentType.GUILD_TAKE_OUT_FAILED);
        }

        // 记录物品变动记录
        goodsRecord(role, guild, goodsId, num, false);

        // 取出成功，邮件发送物品
        EmailGoods ed = new EmailGoods();
        ed.setGid(goodsId);
        ed.setNum(num);
        emailService.sendEmail(role, Lists.newArrayList(ed), EmailType.GUILD_TAKE_OUT);

        // 消息返回
        sendCommonMsg(channel, ContentType.GUILD_TAKE_OUT_SUCCESS);
    }

    /**
     * 更新公会仓库信息
     *
     * @param role      玩家信息
     * @param guild     公会信息
     * @param goodsId   物品信息
     * @param num       数量
     * @param isDonate         变动类型
     */
    private boolean goodsStoreChange(Role role, Guild guild, long goodsId, int num, boolean isDonate) {
        // 用户信息
        Channel ch = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        // 获取公会的物品信息
        List<GuildGoodsStore> goodsStores = GuildCache.getGuildStoreMap().get(guild.getId());
        if (goodsStores == null){
            goodsStores = new ArrayList<>();
        }
        // 判断数据库中是否已有记录
        Optional<GuildGoodsStore> result = goodsStores.stream().filter(gs -> gs.getGoodsId() == goodsId).findFirst();
        if (result.isPresent()){
            GuildGoodsStore gs = result.get();

            // 根据变动类型更新物品数量
            num = isDonate ? num : -num;
            if (gs.getNum() + num < 0){
                sendFailedMsg(ch, ContentType.GUILD_STORE_NO_ENOUGH);
                return false;
            }
            gs.setNum(gs.getNum() + num);

            // 公会物品全部取出，删除数据库记录
            if (gs.getNum() == 0){
                int n = guildGoodsDao.deleteGuildStore(gs);
                log.info("delete guild_goods_store, affected rows: " + n);

                // 移除本地缓存
                goodsStores.removeIf(g -> g.getGoodsId() == goodsId);
            }else {
                // 更新数据库
                int n = guildGoodsDao.updateGuildStore(gs);
                log.info("update guild_goods_store, affected rows: " + n);
            }
        }else {
            if (isDonate){
                GuildGoodsStore gs = new GuildGoodsStore();
                gs.setGuildId(guild.getId());
                gs.setGoodsId(goodsId);
                gs.setNum(num);

                int n = guildGoodsDao.insertGuildStore(gs);
                log.info("insert guild_goods_store, affected rows: " + n);

                goodsStores.add(gs);
            }else {
                // 无该物品，无法取出
                sendFailedMsg(ch, ContentType.GUILD_STORE_NO_ENOUGH);
                return false;
            }
        }

        // 更新本地公会仓库列表
        GuildCache.getGuildStoreMap().put(guild.getId(), goodsStores);
        return true;
    }


    /**
     * isDonate
     *
     * @param role          玩家信息
     * @param guild         公会信息
     * @param goodsId       物品id
     * @param num           物品数量
     * @param isDonate      变动类型
     */
    private void goodsRecord(Role role, Guild guild, long goodsId, int num, boolean isDonate){
        GuildGoodsRecord record = new GuildGoodsRecord();
        record.setRoleId(role.getRoleId());
        record.setGuildId(guild.getId());
        record.setGoodsId(goodsId);
        record.setNum(num);
        record.setCreateTime(new Date());
        record.setType(isDonate ? DONATE : TAKE_OUT);

        // 保存db
        int n = guildGoodsDao.insertGuildGoodsRecord(record);
        log.info("insert guild_goods_record, affected rows: " + n);
    }


    /**
     * 将玩家踢出公会
     *
     * @param req       请求
     * @param channel   channel
     */
    public void kickOut(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 将要踢出的玩家id
        long roleId = req.getRoleId();

        // 获取当前的公会信息
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        // 判断是否有权限踢出玩家
        Member member = GuildCache.getRoleMemberMap().get(role.getRoleId());
        Member kickM = GuildCache.getRoleMemberMap().get(roleId);
        if (member.getPosition() < MemberType.VICE_PRESIDENT.getCode() || kickM.getPosition() >= member.getPosition()){
            sendFailedMsg(channel, ContentType.GUILD_NOT_PERMISSION);
            return;
        }

        // 踢出玩家
        doKickMember(kickM, guild, ContentType.GUILD_HAS_KICK_OUT);

        // 消息回复
        sendCommonMsg(channel, ContentType.GUILD_KICK_OUT_SUCCECSS);
    }


    /**
     * 玩家主动退出公会
     *
     * @param req       请求
     * @param channel   channel
     */
    public void exitGuild(MsgGuildProto.RequestGuild req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        if (role == null){
            return;
        }

        // 获取当前的公会信息
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        Member member = GuildCache.getRoleMemberMap().get(role.getRoleId());
        List<Member> members = guild.getMembers();
        if (members.size() > 1 && member.getPosition() == MemberType.PRESIDENT.getCode()){
            sendFailedMsg(channel, ContentType.GUILD_LEAVE_FAILED_NOT_EMPTY);
            return;
        }

        // 玩家主动
        doKickMember(member, guild, ContentType.GUILD_LEAVE_SUCCESS);
    }



    /**
     * 将玩家踢出公会
     *
     * @param kickM     被踢出的成员信息
     * @param guild     公会信息
     */
    private void doKickMember(Member kickM, Guild guild, String msg) {
        // 获取用户信息
        Role role = LocalUserMap.getIdRoleMap().get(kickM.getRoleId());
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        // 删除对应的记录
        int n = guildDao.deleteMemberInfo(kickM.getRoleId());
        log.info("delete guild_member, affected row: " + n);

        List<Member> members = guild.getMembers();
        members.removeIf(m -> m.getRoleId().longValue() == kickM.getRoleId());
        if (members.size() == 0){
             n = guildDao.deleteGuild(guild);
             log.info("delete guild, affected rows: " + n);

             GuildCache.getIdGuildMap().remove(guild.getId());
        }
        guild.setNum(guild.getNum() - 1);
        n = guildDao.updateGuild(guild);
        log.info("update guild, affected rows: " + n);

        // 移除本地记录
        GuildCache.getRoleIdGuildMap().remove(role.getRoleId());
        GuildCache.getRoleMemberMap().remove(role.getRoleId());
        GuildCache.getIdGuildMap().put(guild.getId(), guild);

        // 返回被踢出的消息
        sendCommonMsg(channel, msg);
    }


    /**
     * 更改权限的具体操作
     *
     * @param member    公会成员信息
     * @param guild     公会信息
     * @param position  权限
     */
    private void doChangePosition(Member member, Guild guild, int position) {
        member.setPosition(position);

        // 更新成员信息
        int n = guildDao.updateMemberByRoleId(member);
        log.info("update guild_member, affected row: " + n);

        // 更新缓存
        GuildCache.getIdGuildMap().put(guild.getId(), guild);
        GuildCache.getRoleIdGuildMap().put(member.getRoleId(), guild);
        GuildCache.getRoleMemberMap().put(member.getRoleId(), member);
    }


    /**
     * 具体的审批操作
     *
     * @param role          玩家信息
     * @param guild         公会信息
     * @param applyId       申请id
     * @param approval      审批结果
     */
    private void doApproval(Role role, Guild guild, long applyId, int approval) {
        // 找到申请记录
        List<GuildApply> applies = GuildCache.getGuildApplyMap().get(guild.getId());
        Optional<GuildApply> result = applies.stream().filter(ga -> ga.getId() == applyId).findFirst();
        if (result.isPresent()){
            GuildApply apply = result.get();
            apply.setApprover(role.getRoleId());
            apply.setModifyTime(new Date());
            apply.setProcess(approval);

            // 更新数据库
            int n = guildApplyDao.updateGuildApply(apply);
            log.info("update guild_apply, affected rows: " + n);

            // 获取申请人信息
            Role applicant = LocalUserMap.getIdRoleMap().get(apply.getRoleId());

            // 删除对应的本地缓存记录
            applies.removeIf(ga -> ga.getId() == applyId);
            List<GuildApply> applyList = GuildCache.getRoleGuildApplyMap().get(applicant.getRoleId());
            applyList.removeIf(a -> a.getId() == applyId);

            // 判断审批结果，并进行消息返回
            doJoinGuild(applicant, guild, approval);
        }
    }


    /**
     * 具体加入公会操作
     *
     * @param applicant     申请人信息
     * @param guild         公会信息
     * @param approval      审批结果
     */
    private void doJoinGuild(Role applicant, Guild guild, int approval){
        // 获取 channel 信息
        Channel channel = ChannelCache.getUserIdChannelMap().get(applicant.getUserId());

        // 判断是否审批通过
        if (approval == APPROVAL_YES){
            Member member = buildMember(applicant, guild, MemberType.ORDINARY);

            // 保存成员信息
            int n = guildDao.insertGuildMember(member);
            log.info("insert guild_member, affected rows: "  + n);

            List<Member> members = guild.getMembers();
            members.add(member);
            guild.setNum(guild.getNum() + 1);

            // 更新公会信息
            n = guildDao.updateGuild(guild);
            log.info("update guild, affrcted rows: " + n);

            // 本地保存
            GuildCache.getRoleMemberMap().put(applicant.getRoleId(), member);
            GuildCache.getIdGuildMap().put(guild.getId(), guild);
            GuildCache.getRoleIdGuildMap().put(applicant.getRoleId(), guild);

            // 加入到公会群聊中
            ChannelGroup cg = ChannelCache.getGuildChannelMap().get(guild.getId());
            cg.add(channel);
            ChannelCache.getGuildChannelMap().put(guild.getId(), cg);

            // 消息通知
            sendCommonMsg(channel, String.format(ContentType.GUILD_APPLY_AGREE, guild.getName()));
        }else {
            sendCommonMsg(channel, String.format(ContentType.GUILD_APPLY_REFUSE, guild.getName()));
        }
    }



    /**
     * 进行公会申请
     *
     * @param role      玩家信息
     * @param guild     公会信息
     */
    private void doGuildApply(Role role, Guild guild) {
        GuildApply apply = new GuildApply();
        apply.setRoleId(role.getRoleId());
        apply.setGuildId(guild.getId());
        apply.setProcess(0);
        apply.setCreateTime(new Date());
        apply.setModifyTime(new Date());

        // 保存申请信息
        int n = guildApplyDao.insertGuildApply(apply);
        log.info("insert guild_apply, affected rows: " + n);

        // 存储公会的申请信息
        List<GuildApply> applyList = Optional.ofNullable(GuildCache.getGuildApplyMap().get(guild.getId()))
                .orElse(Lists.newArrayList());
        applyList.add(apply);
        GuildCache.getGuildApplyMap().put(guild.getId(), applyList);

        // 存储玩家的申请信息
        List<GuildApply> roleApplyList = Optional.ofNullable(GuildCache.getRoleGuildApplyMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        roleApplyList.add(apply);
        GuildCache.getRoleGuildApplyMap().put(role.getRoleId(), roleApplyList);
    }


    /**
     * 创建公会信息
     *
     * @param role          玩家信息
     * @param guildName     公会名
     * @param bulletin      公会公告
     */
    private void doEstablishGuild(Role role, String guildName, String bulletin) {
        // 构建公会实体
        Guild guild =new Guild();
        guild.setName(guildName);
        guild.setBulletin(bulletin);
        guild.setLevel(1);
        guild.setNum(1);
        guild.setMaxNum(20);
        guild.setPresident(role.getRoleId());

        // 保存公会信息
        int n = guildDao.insertGuild(guild);
        log.info("insert guild, affected row: " + n);

        // 构建
        Member member = buildMember(role, guild, MemberType.PRESIDENT);

        // 保存公会成员信息
        n = guildDao.insertGuildMember(member);
        log.info("insert role_guild, affected row: " + n);

        // 设置公会成员属性
        guild.setMembers(Lists.newArrayList(member));

        // 构建公会消息channel
        ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelGroup.add(ChannelCache.getUserIdChannelMap().get(role.getUserId()));
        ChannelCache.getGuildChannelMap().put(guild.getId(), channelGroup);

        // 本地缓存
        GuildCache.getIdGuildMap().put(guild.getId(), guild);
        GuildCache.getRoleIdGuildMap().put(role.getRoleId(), guild);
        GuildCache.getRoleMemberMap().put(role.getRoleId(), member);
    }


    /**
     * 构建公会成员对象
     *
     * @param role      玩家信息
     * @param guild     公会信息
     * @param type      公会成员类型
     * @return          成员信息
     */
    private Member buildMember(Role role, Guild guild,  MemberType type){
        Member member = new Member();
        member.setRoleId(role.getRoleId());
        member.setGid(guild.getId());
        member.setPosition(type.getCode());
        member.setTodayCon(0);
        member.setAllCon(0);

        return member;
    }


    /**
     * 加入公会消息 channelGroup (防止服务器重启，玩家离开channelGroup)
     *
     * @param role  玩家信息
     */
    public void joinGuildChannelGroup(Role role){
        Guild guild = GuildCache.getRoleIdGuildMap().get(role.getRoleId());
        if (guild == null){
            return;
        }

        // 有公会，那么加入公会 channelGroup中
        ChannelGroup cg = Optional.ofNullable(ChannelCache.getGroupChannelMap().get(guild.getId()))
                .orElse(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
        if (cg.contains(channel)){
            return;
        }
        cg.add(channel);
        ChannelCache.getGuildChannelMap().put(guild.getId(), cg);
    }



    /**
     * 获取玩家所在的公会信息
     *
     * @param roleId    玩家信息
     * @return          公会信息
     */
    private Guild getCurGuild(long roleId){
        return GuildCache.getRoleIdGuildMap().get(roleId);
    }


    /**
     * 获取所有的工会信息
     *
     * @return  公会信息列表
     */
    private List<Guild> getAllGuild() {
        return Lists.newArrayList(GuildCache.getIdGuildMap().values());
    }


    /**
     * 构建返回消息
     *
     * @param guilds        公会信息
     * @param applies       公会申请信息
     * @param storeList     公会物品列表
     * @param type          请求类型
     * @return              公会协议返回
     */
    private MsgGuildProto.ResponseGuild combineResp(List<Guild> guilds, List<GuildApply> applies, List<EmailGoods> storeList, MsgGuildProto.RequestType type) {
        guildResp = MsgGuildProto.ResponseGuild.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .addAllGuild(protoService.transToGuildList(guilds))
                .addAllApply(protoService.transToGuildApplyList(applies))
                .addAllGoods(protoService.transToGoodsList(storeList))
                .setType(type)
                .build();
        return guildResp;
    }


    /**
     * 发送公共消息
     *
     * @param channel   channel
     * @param msg       消息
     */
    private void sendCommonMsg(Channel channel, String msg){
        guildResp = MsgGuildProto.ResponseGuild.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(msg)
                .setType(MsgGuildProto.RequestType.COMMON_GUILD)
                .build();
        channel.writeAndFlush(guildResp);
    }


    /**
     * 公会消息
     *
     * @param cg      guildChannelGroup
     * @param msg     消息
     */
    private void sendCommonMsg(ChannelGroup cg, String msg){
        guildResp = MsgGuildProto.ResponseGuild.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(msg)
                .setType(MsgGuildProto.RequestType.COMMON_GUILD)
                .build();
        cg.writeAndFlush(guildResp);
    }



    /**
     * 发送失败消息
     *
     * @param channel       channel
     * @param msg           消息
     */
    private void sendFailedMsg(Channel channel, String msg) {
        guildResp = MsgGuildProto.ResponseGuild.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(guildResp);
    }
}
