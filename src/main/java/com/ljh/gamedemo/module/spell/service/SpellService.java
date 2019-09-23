package com.ljh.gamedemo.module.spell.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.spell.dao.RoleSpellDao;
import com.ljh.gamedemo.module.spell.bean.Partner;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.spell.bean.Spell;
import com.ljh.gamedemo.module.spell.local.LocalSpellMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.spell.cache.PartnerCache;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.proto.protoc.RoleProto;
import com.ljh.gamedemo.proto.protoc.SpellProto;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.module.base.service.ProtoService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 技能服务
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Slf4j
@Service
public class SpellService {

    /**
     * roleSpellDao
     */
    @Autowired
    private RoleSpellDao roleSpellDao;

    /**
     * protoService
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 技能协议返回
     */
    private MsgSpellProto.ResponseSpell response;



    /**
     * 获取玩家角色当前的所有技能信息
     *
     * @param req           请求
     * @param channel       channel
     */
    public void getSpell(MsgSpellProto.RequestSpell req, Channel channel){
        // 获取玩家信息
        Role role = LocalUserMap.userRoleMap.get(req.getUserId());

        // 获取角色的类型
        int type = role.getType();

        // 获取所有的技能，除了已经学习的之外的
        List<Spell> spells = Optional.ofNullable(LocalSpellMap.getTypeSpellMap().get(type))
                .orElse(Lists.newArrayList());
        List<Spell> hasLearn = Optional.ofNullable(LocalSpellMap.getRoleSpellMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());


        // 去除已经学习过的技能信息
        Map<Integer, Spell> setMap = new HashMap<>();
        spells.forEach(s -> setMap.put(s.getSpellId(), s));
        hasLearn.forEach(s -> setMap.remove(s.getSpellId()));

        // 获取可学习的技能列表
        List<Spell> res = new ArrayList<>();
        setMap.forEach((k, v) -> res.add(v));


        // 构造response，包括全部技能和当前已经学习的技能
        response =  MsgSpellProto.ResponseSpell.newBuilder()
                .setType(MsgSpellProto.RequestType.SPELL)
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.SPELL_ALL)
                .setRole(protoService.transToRole(role))
                .addAllSpell(protoService.transToSpellList(res))
                .addAllOwn(protoService.transToSpellList(hasLearn))
                .build();
        channel.writeAndFlush(response);
    }


    /**
     * 学习技能
     * 1.解析参数，获得 userId, spellId
     * 2.获得 role对象，判断用户等级
     * 3.存放 map<roleId, List<spell>>
     * 4.构造response
     *
     * @param req      技能请求
     * @param channel  channel
     */
    public void learn(MsgSpellProto.RequestSpell req, Channel channel){
        // 获取角色信息
        Role role = LocalUserMap.userRoleMap.get(req.getUserId());

        // 获取技能信息
        int spellId = req.getSpellId();
        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);
        if (spell == null){
            responseFailedMsg(channel, ContentType.SPELL_EMPTY);
            return;
        }

        // 技能存在，新增信息至 数据库 & map
        // 更新数据库
        int n = roleSpellDao.insertRoleSpell(role.getRoleId(), spellId);
        log.info("insert into role_spell, affected rows: " + n);


        Map<Long, List<Spell>> spellMap = LocalSpellMap.getRoleSpellMap();
        List<Spell> spellList = Optional.ofNullable(spellMap.get(role.getRoleId())).orElse(Lists.newArrayList());
        spellList.add(spell);
        spellMap.put(role.getRoleId(), spellList);

        // 更新本地缓存
        LocalSpellMap.setRoleSpellMap(spellMap);

        // 构造返回消息
        response =  MsgSpellProto.ResponseSpell.newBuilder()
                .setType(MsgSpellProto.RequestType.LEARN)
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.SPELL_LEARN_SUCCESS)
                .addAllSpell(protoService.transToSpellList(spellList))
                .setRole(protoService.transToRole(role))
                .build();
        channel.writeAndFlush(response);
    }



    /**
     * 技能信息查询拦截
     */
    public MsgSpellProto.ResponseSpell spellStateInterceptor(int spellId){

        // 判断技能信息是否存在的问题
        if (spellId <= 0){
            return MsgSpellProto.ResponseSpell.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ATTACK_SPELL_EMPTY)
                    .build();
        }

        // 找不到该技能
        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);
        if (spell == null){
            return MsgSpellProto.ResponseSpell.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ATTACK_SPELL_NOT_FOUND)
                    .build();
        }
        return null;
    }


    /**
     * 返回失败的消息
     *
     * @param channel   channel
     * @param msg       消息
     */
    private void responseFailedMsg(Channel channel, String msg){
        response = MsgSpellProto.ResponseSpell.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(response);
    }


    /**
     * 通过技能构造一个伙伴实体
     *
     * @param role      玩家信息
     * @param spell     技能信息
     */
    public Partner doSpellCallPartner(Role role, Spell spell){
        Partner partner = new Partner();
        // 设置伙伴id
        partner.setId(spell.getSpellId() + role.getRoleId());
        partner.setRoleId(role.getRoleId());
        partner.setName(String.format(ContentType.SPELL_PARTER_NAME, role.getName()));
        partner.setDamage(spell.getDamage());
        partner.setHp(spell.getUp());

        // 缓存存放
        PartnerCache.getIdPartnerMap().put(partner.getId(), partner);
        PartnerCache.getRolePartnerMap().put(role.getRoleId(), partner);
        return partner;
    }
}
