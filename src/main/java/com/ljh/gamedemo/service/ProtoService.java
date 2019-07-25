package com.ljh.gamedemo.service;

import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.proto.protoc.*;
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

    private static ProtoService protoService;

    public static ProtoService getInstance(){
        if (protoService == null){
            protoService = new ProtoService();
        }
        return protoService;
    }


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

    public CreepProto.Creep transToCreep(Creep creep){
        return CreepProto.Creep.newBuilder()
                .setCreepId(creep.getCreepId())
                .setName(creep.getName())
                .setType(creep.getType())
                .setLevel(creep.getLevel())
                .setNum(creep.getNum())
                .setHp(creep.getHp())
                .setMaxHp(creep.getMaxHp())
                .setDamage(creep.getNum())
                .build();
    }


    public ItemsProto.Items transToItem(Items items){
        return ItemsProto.Items.newBuilder()
                .setItemsId(items.getItemsId())
                .setType(items.getType())
                .setName(items.getName())
                .setNum(items.getNum())
                .setUp(items.getUp())
                .setSec(items.getSec())
                .setDesc(items.getDesc())
                .build();
    }


    public List<ItemsProto.Items> transToItemsList(List<Items> items){
        List<ItemsProto.Items> list = new ArrayList<>();
        for (Items i : items){
            list.add(transToItem(i));
        }
        return list;
    }


    public EquipProto.Equip transToEquip(Equip equip){
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


    public List<EquipProto.Equip> transToEquipList(List<Equip> equips){
        List<EquipProto.Equip> list = new ArrayList<>();
        if (equips == null){
            return list;
        }
        for (Equip e : equips){
            list.add(transToEquip(e));
        }
        return list;
    }

}
