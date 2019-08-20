package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.RoleSpellDao;
import com.ljh.gamedemo.entity.Partner;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.local.LocalSpellMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.PartnerCache;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.proto.protoc.RoleProto;
import com.ljh.gamedemo.proto.protoc.SpellProto;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: Heiku
 * @Date: 2019/7/11
 */
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
     * userService
     */
    @Autowired
    private UserService userService;


    /**
     * 技能协议返回
     */
    private MsgSpellProto.ResponseSpell response;

    /**
     * 账号协议返回
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    /**
     * 获取玩家角色当前的所有技能信息
     *
     * @param requestSpell
     * @return
     */
    public void getSpell(MsgSpellProto.RequestSpell requestSpell, Channel channel){

        // 用户角色认证
        userResp = userService.userStateInterceptor(requestSpell.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 获取请求参数
        long userId = requestSpell.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);

        // 获取角色的类型
        int type = role.getType();

        // 获取所有的技能，除了已经学习的之外的
        List<Spell> spells = LocalSpellMap.getTypeSpellMap().get(type);
        List<Spell> hasLearn = LocalSpellMap.getRoleSpellMap().get(role.getRoleId());
        // 去重
        Map<Integer, Spell> setMap = new HashMap<>();
        for (Spell s : spells){
            setMap.put(s.getSpellId(), s);
        }
        if (hasLearn != null && !hasLearn.isEmpty()) {
            for (Spell h : hasLearn) {
                setMap.remove(h.getSpellId());
            }
        }
        List<Spell> res = new ArrayList<>();
        setMap.forEach((k, v) -> res.add(v));


        // 构造response，包括全部技能和当前已经学习的技能
        RoleProto.Role r = protoService.transToRole(role);
        List<SpellProto.Spell> spellList = protoService.transToSpellList(res);
        List<SpellProto.Spell> ownList = new ArrayList<>();
        if (hasLearn != null && !hasLearn.isEmpty()){
            ownList = protoService.transToSpellList(hasLearn);
        }

        response =  MsgSpellProto.ResponseSpell.newBuilder()
                .setType(MsgSpellProto.RequestType.SPELL)
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.SPELL_ALL)
                .setRole(r)
                .addAllSpell(spellList)
                .addAllOwn(ownList)
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
     * @param requestSpell
     * @return
     */
    public void learn(MsgSpellProto.RequestSpell requestSpell, Channel channel){
        // 用户状态认证
        userResp = userService.userStateInterceptor(requestSpell.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return ;
        }

        // 获取角色信息
        long userId = requestSpell.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);
        long roleId = role.getRoleId();

        // 获取技能信息
        int spellId = requestSpell.getSpellId();
        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);
        if (spell == null){
            responseFailedMsg(channel, ContentType.SPELL_EMPTY);
            return;
        }

        // 技能存在，新增信息至 数据库 & map
        // 更新数据库
        int n = roleSpellDao.insertRoleSpell(roleId, spellId);
        if (n <= 0){
            responseFailedMsg(channel, ContentType.INSERT_FAILED);
            return;
        }
        Map<Long, List<Spell>> spellMap = LocalSpellMap.getRoleSpellMap();
        List<Spell> spellList = spellMap.get(roleId);
        if (spellList == null){
            spellList = new ArrayList<>();
        }
        spellList.add(spell);
        spellMap.put(roleId, spellList);

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
     * @param role
     * @param spell
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
