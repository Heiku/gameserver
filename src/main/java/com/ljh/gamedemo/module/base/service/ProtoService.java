package com.ljh.gamedemo.module.base.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.duplicate.bean.Boss;
import com.ljh.gamedemo.module.duplicate.bean.BossSpell;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.email.bean.Email;
import com.ljh.gamedemo.module.email.bean.EmailGoods;
import com.ljh.gamedemo.module.entity.bean.Entity;
import com.ljh.gamedemo.module.equip.bean.Equip;
import com.ljh.gamedemo.module.equip.local.LocalEquipMap;
import com.ljh.gamedemo.module.face.bean.Transaction;
import com.ljh.gamedemo.module.goods.bean.Goods;
import com.ljh.gamedemo.module.goods.local.LocalGoodsMap;
import com.ljh.gamedemo.module.group.bean.Group;
import com.ljh.gamedemo.module.guild.bean.Guild;
import com.ljh.gamedemo.module.guild.bean.GuildApply;
import com.ljh.gamedemo.module.guild.bean.Member;
import com.ljh.gamedemo.module.items.bean.Items;
import com.ljh.gamedemo.module.items.local.LocalItemsMap;
import com.ljh.gamedemo.module.mall.bean.Commodity;
import com.ljh.gamedemo.module.pk.bean.PKRecord;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.bean.RoleInit;
import com.ljh.gamedemo.module.spell.bean.Spell;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.bean.Task;
import com.ljh.gamedemo.module.task.cache.TaskCache;
import com.ljh.gamedemo.module.trade.bean.Trade;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.*;
import io.netty.channel.Channel;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 实体类型 -> proto type
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Service
public class ProtoService {

    private static ProtoService protoService;

    public static ProtoService getInstance(){
        if (protoService == null){
            protoService = new ProtoService();
        }
        return protoService;
    }


    /**
     * proto Role
     *
     * @param role      玩家信息
     * @return          玩家协议
     */
    public RoleProto.Role transToRole(Role role){
        if (role == null){
            return RoleProto.Role.newBuilder()
                    .build();
        }

        return RoleProto.Role.newBuilder()
                .setRoleId(role.getRoleId())
                .setName(role.getName())
                .setType(role.getType())
                .setLevel(role.getLevel())
                .setAlive(role.getAlive())
                .setHp(role.getHp())
                .setMp(role.getMp())
                .setGold(role.getGold())
                .setHonor(role.getHonor())
                .build();
    }


    /**
     * proto RoleList
     *
     * @param roleList      玩家列表
     * @return              玩家协议列表
     */
    public List<RoleProto.Role> transToRoleList(List<Role> roleList){
        List<RoleProto.Role> roles = Lists.newArrayList();
        if (CollectionUtils.isEmpty(roleList)){
            return roles;
        }

        roleList.forEach(r -> roles.add(transToRole(r)));
        return roles;
    }


    /**
     * proto RoleList
     *
     * @param roleIdList        玩家id列表
     * @return                  玩家协议列表
     */
    public List<RoleProto.Role> transToRoleListById(List<Long> roleIdList){
        List<RoleProto.Role> res = new ArrayList<>();
        if (roleIdList == null || roleIdList.isEmpty()){
            return res;
        }

        for (Long id : roleIdList) {
            Role role = LocalUserMap.getIdRoleMap().get(id);
            RoleProto.Role r = transToRole(role);
            res.add(r);
        }

        return res;
    }



    /**
     * proto SpellList
     *
     * @param res       技能列表
     * @return          技能协议列表
     */
    public List<SpellProto.Spell> transToSpellList(List<Spell> res){
        List<SpellProto.Spell> spellList = new ArrayList<>();
        if (CollectionUtils.isEmpty(res)){
            return spellList;
        }
        res.forEach(s -> spellList.add(transToSpell(s)));

        return spellList;
    }


    /**
     * proto Spell
     *
     * @param spell     技能信息
     * @return          技能协议
     */
    public SpellProto.Spell transToSpell(Spell spell){
        return SpellProto.Spell.newBuilder()
                .setSpellId(spell.getSpellId())
                .setName(spell.getName())
                .setLevel(spell.getLevel())
                .setCoolDown(spell.getCoolDown())
                .setCost(spell.getCost())
                .setDamage(spell.getDamage())
                .setRange(spell.getRange())
                .build();
    }


    /**
     * proto Crepp
     *
     * @param creep     野怪信息
     * @return          野怪协议
     */
    public CreepProto.Creep transToCreep(Creep creep){
        return CreepProto.Creep.newBuilder()
                .setId(creep.getId())
                .setCreepId(creep.getCreepId())
                .setName(creep.getName())
                .setType(creep.getType())
                .setLevel(creep.getLevel())
                .setHp(creep.getHp())
                .setMaxHp(creep.getMaxHp())
                .setDamage(creep.getDamage())
                .build();
    }


    /**
     * proto creepList
     *
     * @param creeps        野怪列表
     * @return              野怪协议列表
     */
    public List<CreepProto.Creep> transToCreepList(List<Creep> creeps) {
        List<CreepProto.Creep> creepList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(creeps)){
            return creepList;
        }
        creeps.forEach(c -> creepList.add(transToCreep(c)));
        return creepList;
    }


    /**
     * proto Items
     *
     * @param items     物品信息
     * @return          物品协议
     */
    public ItemsProto.Items transToItem(Items items){
        if (items == null){
            return ItemsProto.Items.newBuilder().build();
        }
        return ItemsProto.Items.newBuilder()
                .setItemsId(items.getItemsId())
                .setType(items.getType())
                .setName(items.getName())
                .setNum(Optional.ofNullable(items.getNum()).orElse(1))
                .setUp(items.getUp())
                .setSec(items.getSec())
                .setDesc(items.getDesc())
                .build();
    }


    /**
     * proto ItemsList
     *
     * @param items     物品信息列表
     * @return          物品协议列表
     */
    public List<ItemsProto.Items> transToItemsList(List<Items> items){
        List<ItemsProto.Items> list = new ArrayList<>();
        if (items == null || items.isEmpty()){
            return list;
        }
        for (Items i : items){
            list.add(transToItem(i));
        }
        return list;
    }


    /**
     * proto Equip
     *
     * @param equip     装备信息
     * @return          装备协议
     */
    public EquipProto.Equip transToEquip(Equip equip){
        if (equip == null){
            return EquipProto.Equip.newBuilder().build();
        }
        return EquipProto.Equip.newBuilder()
                .setEquipId(equip.getEquipId())
                .setName(equip.getName())
                .setType(equip.getType())
                .setLevel(equip.getLevel())
                .setPart(equip.getPart())
                .setDurability(equip.getDurability())
                .setState(equip.getState())
                .setAUp(equip.getAUp())
                .setSpUp(equip.getSpUp())
                .setHpUp(equip.getHpUp())
                .build();
    }


    /**
     * proto EquipList
     *
     * @param equips        装备列表
     * @return              装备协议列表
     */
    public List<EquipProto.Equip> transToEquipList(List<Equip> equips){
        List<EquipProto.Equip> list = new ArrayList<>();
        if (equips == null || equips.isEmpty()){
            return list;
        }
        for (Equip e : equips){
            list.add(transToEquip(e));
        }
        return list;
    }


    /**
     * proto DuplicateList
     *
     * @param duplicates        副本列表
     * @return                  副本协议列表
     */
    public List<DuplicateProto.Duplicate> transToDuplicateList(List<Duplicate> duplicates) {
        List<DuplicateProto.Duplicate> list = new ArrayList<>();
        if (duplicates == null){
            return list;
        }
        for (Duplicate duplicate : duplicates) {
            list.add(transToDuplicate(duplicate));
        }
        return list;
    }


    /**
     * proto Duplicate
     *
     * @param duplicate     副本信息
     * @return              副本协议
     */
    public DuplicateProto.Duplicate transToDuplicate(Duplicate duplicate) {
        return DuplicateProto.Duplicate.newBuilder()
                .setId(duplicate.getId())
                .setName(duplicate.getName())
                .setGoldReward(duplicate.getGoldReward())
                .setProgress(duplicate.getProgress())
                .setLimitTime(duplicate.getLimitTime())
                .addAllBosses(transToBossList(duplicate.getBosses()))
                .addAllEquips(transToEquipList(duplicate.getEquipReward()))
                .build();
    }


    /**
     * proto BossList
     *
     * @param bosses        Boss列表
     * @return              Boss协议列表
     */
    public List<BossProto.Boss> transToBossList(List<Boss> bosses){
        List<BossProto.Boss> list = new ArrayList<>();
        if (bosses == null){
            return list;
        }
        bosses.forEach(boss -> list.add(transToBoss(boss)));

        return list;
    }


    /**
     * proto Boss
     *
     * @param boss      boss信息
     * @return          boss协议信息
     */
    public BossProto.Boss transToBoss(Boss boss) {
        return BossProto.Boss.newBuilder()
                .setId(boss.getId())
                .setName(boss.getName())
                .setHp(boss.getHp())
                .addAllSpell(transToBossSpellList(boss.getSpellList()))
                .build();
    }


    /**
     * proto bossSpellList
     *
     * @param bossSpells        boss技能列表
     * @return                  boss技能协议列表
     */
    private List<SpellProto.Spell> transToBossSpellList(List<BossSpell> bossSpells){
        List<SpellProto.Spell> list = new ArrayList<>();
        if (bossSpells == null){
            return list;
        }
        bossSpells.forEach(bossSpell -> list.add(transToSpell(bossSpell)));
        return list;
    }


    /**
     * proto BossSpell
     *
     * @param bossSpell     boss技能信息
     * @return              boss技能协议信息
     */
    private SpellProto.Spell transToSpell(BossSpell bossSpell){
        return SpellProto.Spell.newBuilder()
                .setSpellId(bossSpell.getSpellId().intValue())
                .setName(bossSpell.getName())
                .setCoolDown(bossSpell.getCd())
                .setDamage(bossSpell.getDamage())
                .setRange(bossSpell.getRange())

                // level -> school
                .setLevel(bossSpell.getSchool())

                // cost -> sec
                .setCost(bossSpell.getSec())
                .build();
    }


    /**
     * proto CommodityList
     *
     * @param list      商品列表
     * @return          商品协议列表
     */
    public List<CommodityProto.Commodity> transToCommodityList(List<Commodity> list) {
        List<CommodityProto.Commodity> res = new ArrayList<>();

        list.forEach(e -> {
            CommodityProto.Commodity c = transToCommodity(e);
            res.add(c);
        });

        return res;
    }


    /**
     * proto Commodity
     *
     * @param e     商品信息
     * @return      商品协议信息
     */
    private CommodityProto.Commodity transToCommodity(Commodity e) {
        if (Objects.isNull(e)){
            return CommodityProto.Commodity.newBuilder().build();
        }
        return CommodityProto.Commodity.newBuilder()
                .setId(e.getId())
                .setType(e.getType())
                .setLimit(e.getLimit())
                .setPrice(e.getPrice())
                .setItem(transToItem(e.getItems()))
                .setEquip(transToEquip(e.getEquip()))
                .build();
    }


    /**
     * proto EmailGoodsList
     *
     * @param egList        物品列表
     * @return              物品协议列表
     */
    public List<GoodsProto.Goods> transToGoodsList(List<EmailGoods> egList) {
        List<GoodsProto.Goods> res = new ArrayList<>();

        if (egList == null || egList.isEmpty()){
            return res;
        }

        egList.forEach( e -> {
            GoodsProto.Goods g = transToGoods(e);
            res.add(g);
        });

        return res;
    }


    /**
     * proto Goods
     *
     * @param e     邮件物品信息
     * @return      协议物品信息
     */
    private GoodsProto.Goods transToGoods(EmailGoods e) {


        Goods goods = LocalGoodsMap.getIdGoodsMap().get(e.getGid());
        if (goods.getType().intValue() == CommodityType.ITEM.getCode()){
            Items i = LocalItemsMap.getIdItemsMap().get(goods.getGid());
            return GoodsProto.Goods.newBuilder()
                    .setNum(e.getNum())
                    .setItem(transToItem(i))
                    .build();

        }else if (goods.getType().intValue() == CommodityType.EQUIP.getCode()){
           Equip eq = LocalEquipMap.getIdEquipMap().get(goods.getGid());
            return GoodsProto.Goods.newBuilder()
                    .setNum(e.getNum())
                    .setEquip(transToEquip(eq))
                    .build();
        }
        return null;
    }


    /**
     * proto Email
     *
     * @param r             玩家信息
     * @param email         邮件信息
     * @param goodsList     物品信息列表
     * @return              邮件协议信息
     */
    public EmailProto.Email transToEmail(Role r, Email email, List<GoodsProto.Goods> goodsList) {
        return EmailProto.Email.newBuilder()
                .setId(email.getId())
                .setFromId(email.getFromId())
                .setTheme(email.getTheme())
                .setContent(email.getContent())
                .setRole(transToRole(r))
                .addAllGoods(goodsList)
                .build();
    }


    /**
     * proto EmailList
     *
     * @param emailGoodsMap     邮件列表
     * @param role              玩家信息
     * @return                  邮件协议列表
     */
    public List<EmailProto.Email> transToEmailList(Map<Email, List<EmailGoods>> emailGoodsMap, Role role) {
        List<EmailProto.Email> resList = new ArrayList<>();

        emailGoodsMap.forEach((e, egList) -> {
            EmailProto.Email emailProto = transToEmail(role, e, transToGoodsList(egList));
            resList.add(emailProto);
        });

        return resList;
    }


    /**
     * proto PKRecord
     *
     * @param record     pk记录信息
     * @return           pk记录协议信息
     */
    public PKProto.PKRecord transToPkRecord(PKRecord record) {
        if (record == null){
            return null;
        }

        return PKProto.PKRecord.newBuilder()
                .setId(record.getId())
                .setWinner(transToRole(LocalUserMap.getIdRoleMap().get(record.getWinner())))
                .setLoser(transToRole(LocalUserMap.getIdRoleMap().get(record.getLoser())))
                .setWinHonor(record.getWinHonor())
                .setLoseHonor(record.getLoseHonor())
                .build();
    }


    /**
     * proto Group
     *
     * @param group     队伍消息
     * @return          队伍协议消息
     */
    public GroupProto.Group transToGroup(Group group) {
        if (group == null){
            return null;
        }
        Role leader = LocalUserMap.getIdRoleMap().get(group.getLeader());
        List<RoleProto.Role> roles = transToRoleListById(group.getMembers());

        return GroupProto.Group.newBuilder()
                .setGroupId(group.getId())
                .setLeader(transToRole(leader))
                .addAllMembers(roles)
                .build();
    }


    /**
     * proto RoleInitList
     *
     * @param roleInits     玩家初始化列表
     * @return              玩家初始化协议列表
     */
    public List<RoleInitProto.RoleInit> transToRoleInitList(List<RoleInit> roleInits){
        List<RoleInitProto.RoleInit> resList = new ArrayList<>();
        if (roleInits == null || roleInits.isEmpty()){
            return resList;
        }
        roleInits.forEach(r -> resList.add(transToRoleInit(r)));
        return resList;
    }


    /**
     * proto RoleInit
     *
     * @param r     玩家初始化信息
     * @return      玩家初始化协议信息
     */
    private RoleInitProto.RoleInit transToRoleInit(RoleInit r) {
        if (r == null){
            return null;
        }
        return RoleInitProto.RoleInit.newBuilder()
                .setType(r.getType())
                .setName(r.getName())
                .setHp(r.getHp())
                .setMp(r.getMp())
                .setDesc(r.getDesc())
                .build();
    }


    /**
     * proto Transaction
     *
     * @param trans     交易信息
     * @return          交易协议信息
     */
    public TransProto.Trans transToTransaction(Transaction trans) {
        if (trans == null){
            return null;
        }

        EmailGoods emailGoods = new EmailGoods();
        emailGoods.setGid(trans.getGoodsId());
        emailGoods.setNum(trans.getNum());

        return TransProto.Trans.newBuilder()
                .setId(trans.getId())
                .setPromoter(transToRole(LocalUserMap.getIdRoleMap().get(trans.getPromoter())))
                .setReceiver(transToRole(LocalUserMap.getIdRoleMap().get(trans.getReceiver())))
                .setGoods(transToGoods(emailGoods))
                .setAmount(trans.getAmount())
                .build();
    }


    /**
     * proto GuildList
     *
     * @param guildList     公会列表
     * @return              公会协议列表
     */
    public List<GuildProto.Guild> transToGuildList(List<Guild> guildList) {
        List<GuildProto.Guild> guilds = new ArrayList<>();
        if (guildList == null || guildList.isEmpty()){
            return guilds;
        }
        guildList.forEach(e -> {
            List<GuildMemberProto.GuildMember> members = new ArrayList<>();
            e.getMembers().forEach(m -> members.add(transToMember(m)));

            GuildProto.Guild g = GuildProto.Guild.newBuilder()
                    .setGuildId(e.getId())
                    .setName(e.getName())
                    .setBulletin(e.getBulletin())
                    .setLevel(e.getLevel())
                    .setNum(e.getNum())
                    .setMaxNum(e.getMaxNum())
                    .setPresident(transToRole(LocalUserMap.getIdRoleMap().get(e.getPresident())))
                    .addAllMember(members)
                    .build();

            guilds.add(g);
        });
        return guilds;
    }


    /**
     * proto member
     *
     * @param m     公会成员信息
     * @return      公会成员协议信息
     */
    private GuildMemberProto.GuildMember transToMember(Member m) {
        if (Objects.isNull(m)){
            return GuildMemberProto.GuildMember.newBuilder().build();
        }

        return GuildMemberProto.GuildMember.newBuilder()
                .setRole(transToRole(LocalUserMap.getIdRoleMap().get(m.getRoleId())))
                .setPosition(m.getPosition())
                .setToday(m.getTodayCon())
                .setAll(m.getAllCon())
                .build();
    }


    /**
     * proto GuildApplyList
     *
     * @param applies   公会申请列表
     * @return          公会申请协议列表
     */
    public List<GuildApplyProto.GuildApply> transToGuildApplyList(List<GuildApply> applies) {
        List<GuildApplyProto.GuildApply> applyList = new ArrayList<>();
        if (applies == null){
            return applyList;
        }
        applies.forEach(a -> applyList.add(transToGuildApply(a)));
        return applyList;
    }


    /**
     * proto GuildApply
     *
     * @param a     公会申请信息
     * @return      公会申请协议信息
     */
    private GuildApplyProto.GuildApply transToGuildApply(GuildApply a) {
        return  GuildApplyProto.GuildApply.newBuilder()
                .setId(a.getId())
                .setApplicant(transToRole(LocalUserMap.getIdRoleMap().get(a.getRoleId())))
                .setApplyTime(a.getCreateTime().toString())
                .build();
    }


    /**
     * proto entity
     *
     * @param entity        实体信息
     * @return              实体协议
     */
    public EntityProto.Entity transToEntity(Entity entity){
        if (entity == null){
            return null;
        }
        return EntityProto.Entity.newBuilder()
                .setName(entity.getName())
                .setId(entity.getId())
                .setType(entity.getType())
                .setAlive(entity.getAlive())
                .setLevel(entity.getLevel())
                .build();
    }


    /**
     * proto entityList
     *
     * @param entities      实体列表
     * @return              实体协议列表
     */
    public List<EntityProto.Entity> transToEntityList(List<Entity> entities) {
        List<EntityProto.Entity> entityList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(entities)){
            return entityList;
        }
        entities.forEach(e -> entityList.add(transToEntity(e)));

        return entityList;
    }


    /**
     * proto tradeList
     *
     * @param trades        交易列表
     * @return              交易协议列表
     */
    public List<TradeProto.Trade> transToTradeList(List<Trade> trades) {
        List<TradeProto.Trade> tradeList = Lists.newLinkedList();
        if (trades == null || trades.isEmpty()){
            return tradeList;
        }

        trades.forEach(t -> tradeList.add(transToTrade(t)));
        return tradeList;
    }


    /**
     * proto trade
     *
     * @param t     交易实体
     * @return      交易协议实体
     */
    private TradeProto.Trade transToTrade(Trade t) {
        if (t == null){
            return TradeProto.Trade.newBuilder().build();
        }
        EmailGoods eg = new EmailGoods();
        eg.setGid(t.getGoodsId());
        eg.setNum(1);

        Date now = new Date();
        Date end = t.getEndTime();
        int remain = 0;
        if (now.getTime() <= end.getTime()) {
            Interval in = new Interval(now.getTime(), end.getTime());
            Period p = in.toPeriod();
            remain= p.getMinutes() * 60 + p.getSeconds();
        }

        return TradeProto.Trade.newBuilder()
                .setTradeId(t.getId())
                .setBuyer(protoService.transToRole(LocalUserMap.getIdRoleMap().get(t.getBuyer())))
                .setSeller(protoService.transToRole(LocalUserMap.getIdRoleMap().get(t.getSeller())))
                .setGoods(transToGoods(eg))
                .setPrice(t.getPrice())
                .setType(t.getType())
                .setCreateTime(t.getStartTime().toString())
                .setRemainTime(remain)
                .build();
    }


    /**
     * proto taskList
     *
     * @param allTask   任务列表
     * @return          任务协议列表
     */
    public List<TaskProto.Task> transToTaskList(List<RoleTask> allTask) {
        List<TaskProto.Task> tasks = Lists.newArrayList();
        if (CollectionUtils.isEmpty(allTask)){
            return tasks;
        }
        allTask.forEach(t -> tasks.add(transToTask(t)));
        return tasks;
    }



    /**
     * proto task
     *
     * @param task      任务实体
     * @return          任务协议实体
     */
    private TaskProto.Task transToTask(RoleTask task) {
        if (Objects.isNull(task)){
            return TaskProto.Task.newBuilder().build();
        }
        Task t = TaskCache.getIdTaskMap().get(task.getTaskId());

        return TaskProto.Task.newBuilder()
                .setId(Optional.ofNullable(task.getId()).orElse(0L))
                .setTaskId(t.getTaskId())
                .setName(t.getName())
                .setDesc(t.getDesc())
                .setGold(t.getGold())
                .setType(t.getType())
                .setState(task.getProgress())
                .addAllGoods(transToGoodsList(t.getGoods()))
                .build();
    }


    /**
     * 返回玩家攻击消息
     *
     * @param role      玩家信息
     * @param msg       消息
     */
    public void sendAttackedMsg(Role role, String msg){
        // 获取channel
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep
                .newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgAttackCreepProto.RequestType.ATTACK)
                .setContent(msg)
                .setRole(transToRole(role))
                .build();
        channel.writeAndFlush(response);
    }




    /**
     * 发送公共消息
     *
     * @param channel       channel
     * @param msg           消息
     */
    public void sendCommonMsg(Channel channel, String msg){
         MessageBase.Message base = MessageBase.Message.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(msg)
                .build();
         channel.writeAndFlush(base);
    }


    /**
     * 发送失败消息
     *
     * @param channel       channel
     * @param msg           消息
     */
    public void sendFailedMsg(Channel channel, String msg){
        MessageBase.Message base = MessageBase.Message.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(base);
    }


}
