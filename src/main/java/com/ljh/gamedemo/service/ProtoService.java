package com.ljh.gamedemo.service;

import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.proto.protoc.RoleProto;
import com.ljh.gamedemo.proto.protoc.SpellProto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体类型 -> proto type
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Service
public class ProtoService {


    public RoleProto.Role transToRole(Role role){

        return RoleProto.Role.newBuilder()
                .setRoleId(role.getRoleId())
                .setName(role.getName())
                .setType(role.getType())
                .setLevel(role.getLevel())
                .setAlive(role.getAlive())
                .setHp(role.getHp())
                .setMp(role.getMp())
                .build();
    }

    public List<SpellProto.Spell> transToSpellList(List<Spell> res){
        List<SpellProto.Spell> spellList = new ArrayList<>();
        for (Spell spell : res){
            spellList.add(transToSpell(spell));
        }

        return spellList;
    }


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
}