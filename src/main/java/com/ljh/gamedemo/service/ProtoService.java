package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.LocalEquipMap;
import com.ljh.gamedemo.local.LocalGoodsMap;
import com.ljh.gamedemo.local.LocalItemsMap;
import com.ljh.gamedemo.proto.protoc.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if (role == null){
            return null;
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
        if (items == null){
            return ItemsProto.Items.newBuilder().build();
        }
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
        if (items == null || items.isEmpty()){
            return list;
        }
        for (Items i : items){
            list.add(transToItem(i));
        }
        return list;
    }


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


    public List<BossProto.Boss> transToBossList(List<Boss> bosses){
        List<BossProto.Boss> list = new ArrayList<>();
        if (bosses == null){
            return list;
        }
        bosses.forEach(boss -> list.add(transToBoss(boss)));

        return list;
    }

    public BossProto.Boss transToBoss(Boss boss) {
        return BossProto.Boss.newBuilder()
                .setId(boss.getId())
                .setName(boss.getName())
                .setHp(boss.getHp())
                .addAllSpell(transToBossSpellList(boss.getSpellList()))
                .build();
    }


    private List<SpellProto.Spell> transToBossSpellList(List<BossSpell> bossSpells){
        List<SpellProto.Spell> list = new ArrayList<>();
        if (bossSpells == null){
            return list;
        }
        bossSpells.forEach(bossSpell -> list.add(transToSpell(bossSpell)));
        return list;
    }


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


    public List<CommodityProto.Commodity> transToCommodityList(List<Commodity> list) {
        List<CommodityProto.Commodity> res = new ArrayList<>();

        list.forEach(e -> {
            CommodityProto.Commodity c = transToCommodity(e);
            res.add(c);
        });

        return res;
    }

    private CommodityProto.Commodity transToCommodity(Commodity e) {
        CommodityProto.Commodity c = CommodityProto.Commodity.newBuilder()
                .setId(e.getId())
                .setType(e.getType())
                .setLimit(e.getLimit())
                .setPrice(e.getPrice())
                .setItem(transToItem(e.getItems()))
                .setEquip(transToEquip(e.getEquip()))
                .build();

        return c;
    }



    public List<GoodsProto.Goods> transToGoodsList(List<EmailGoods> egList) {
        List<GoodsProto.Goods> res = new ArrayList<>();

        if (egList == null || egList.isEmpty()){
            return null;
        }

        egList.forEach( e -> {
            GoodsProto.Goods g = transToGoods(e);
            res.add(g);
        });

        return res;
    }

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



    public List<EmailProto.Email> transToEmailList(Map<Email, List<EmailGoods>> emailGoodsMap, Role role) {
        List<EmailProto.Email> resList = new ArrayList<>();

        emailGoodsMap.forEach((e, egList) -> {
            EmailProto.Email emailProto = transToEmail(role, e, transToGoodsList(egList));
            resList.add(emailProto);
        });

        return resList;
    }
}
