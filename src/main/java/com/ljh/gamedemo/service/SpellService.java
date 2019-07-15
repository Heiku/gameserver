package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.RoleSpellDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.local.LocalSpellMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.proto.protoc.RoleProto;
import com.ljh.gamedemo.proto.protoc.SpellProto;
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

    @Autowired
    private RoleSpellDao roleSpellDao;

    @Autowired
    private ProtoService protoService;

    private MsgSpellProto.ResponseSpell response;

    public MsgSpellProto.ResponseSpell getSpell(MsgSpellProto.RequestSpell requestSpell){

        // 用户角色认证
        response = userStateInterceptor(requestSpell);
        if (response != null){
            return response;
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
        setMap.forEach((k, v) -> {
            res.add(v);
        });


        // 构造response，包括全部技能和当前已经学习的技能
        RoleProto.Role r = protoService.transToRole(role);
        List<SpellProto.Spell> spellList = protoService.transToSpellList(res);
        List<SpellProto.Spell> ownList = new ArrayList<>();
        if (hasLearn != null && !hasLearn.isEmpty()){
            ownList = protoService.transToSpellList(hasLearn);
        }

        return MsgSpellProto.ResponseSpell.newBuilder()
                .setType(MsgSpellProto.RequestType.SPELL)
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.SPELL_ALL)
                .setRole(r)
                .addAllSpell(spellList)
                .addAllOwn(ownList)
                .build();
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
    public MsgSpellProto.ResponseSpell learn(MsgSpellProto.RequestSpell requestSpell){
        // 用户状态认证
        response = userStateInterceptor(requestSpell);
        if (response != null){
            return response;
        }

        // 获取角色信息
        long userId = requestSpell.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);
        long roleId = role.getRoleId();

        // 获取技能信息
        int spellId = requestSpell.getSpellId();
        Spell spell = LocalSpellMap.getIdSpellMap().get(spellId);
        if (spell == null){
            return MsgSpellProto.ResponseSpell.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.SPELL_EMPTY)
                    .build();
        }

        // 技能存在，新增信息至 数据库 & map
        // 更新数据库
        int n = roleSpellDao.insertRoleSpell(roleId, spellId);
        if (n <= 0){
            return MsgSpellProto.ResponseSpell.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.INSERT_FAILED)
                    .build();
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
        return MsgSpellProto.ResponseSpell.newBuilder()
                .setType(MsgSpellProto.RequestType.LEARN)
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.SPELL_LEARN_SUCCESS)
                .addAllSpell(protoService.transToSpellList(spellList))
                .setRole(protoService.transToRole(role))
                .build();

    }

    private MsgSpellProto.ResponseSpell userStateInterceptor(MsgSpellProto.RequestSpell requestSpell){
        // 用户id标识判断
        long userId = requestSpell.getUserId();
        if (userId <= 0){
            return MsgSpellProto.ResponseSpell.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }
        // 找不到对应的角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return MsgSpellProto.ResponseSpell.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }

        return null;
    }

}
