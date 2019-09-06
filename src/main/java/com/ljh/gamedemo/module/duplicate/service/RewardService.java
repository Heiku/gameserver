package com.ljh.gamedemo.module.duplicate.service;

import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.equip.bean.Equip;
import com.ljh.gamedemo.module.equip.service.EquipService;
import com.ljh.gamedemo.module.goods.bean.Goods;
import com.ljh.gamedemo.module.group.bean.Group;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.module.base.service.ProtoService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 发送奖励服务
 *
 * @Author: Heiku
 * @Date: 2019/8/21
 */

@Service
public class RewardService {

    /**
     * 玩家服务
     */
    @Autowired
    private RoleService roleService;

    /**
     * 装备服务
     */
    @Autowired
    private EquipService equipService;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 协议返回
     */
    private MsgDuplicateProto.ResponseDuplicate dupResp;

    /**
     * 为玩家发放奖励，这里是立即得到的物品，不做队列处理
     *
     * @param role
     * @param dup
     */
    public void sendRoleReward(Role role, Duplicate dup) {

        // 奖励副本通关信息
        int gold = dup.getGoldReward();
        Equip e = getDupRewardEquip(dup);
        if (e == null){
            return;
        }
        List<Equip> equips = new ArrayList<>();
        equips.add(e);

        // 构造物品列表
        List<Goods> gList = new ArrayList<>();
        Goods goods = new Goods();
        goods.setGid(e.getEquipId());
        gList.add(goods);

        // 更新玩家的金币信息
        role.setGold(role.getGold() + gold);
        roleService.updateRoleInfo(role);

        // 更新玩家的装备信息
        equipService.addRoleEquips(role, gList);

        // 同时返回给玩家奖励信息
        dupResp = combineDupMsg(dup, equips);
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
        channel.writeAndFlush(dup);
    }


    /**
     * 获取爆出的装备信息
     *
     * @param dup   副本信息
     * @return      奖励的装备信息
     */
    private Equip getDupRewardEquip(Duplicate dup) {
        // 获取装备概率信息
        List<Double> proList = dup.getProbability();
        List<Equip> equipList = dup.getEquipReward();

        // 获取抽奖池总数
        int sum = 0;
        for (Double d : proList) {
            sum += d * 10;
        }

        // 初始化抽奖池
        List<Long> box = new ArrayList<>(sum);
        int idx = 0;
        for (int i = 0; i < proList.size(); i++) {
            int b = (int) (proList.get(i) * 10);
            for (int j = 0; j < b; j++){
                box.add(idx++, equipList.get(i).getEquipId());
            }
        }

        int d = new Random().nextInt(sum);
        // 爆出的装备id
        long yes = box.get(d);
        for (Equip e : equipList) {
            if (e.getEquipId() == yes){
                return e;
            }
        }
        return null;
    }


    /**
     * 组队发放奖励
     *
     * @param group     组队信息
     * @param dup       副本信息
     */
    public void sendGroupReward(Group group, Duplicate dup){
        // 获取奖励信息
        int eachGold = dup.getGoldReward() / group.getMembers().size();
        List<Equip> equips = dup.getEquipReward();

        // 为队伍成员发放奖励
        for (Long m : group.getMembers()) {
            Role r = LocalUserMap.getIdRoleMap().get(m);
            r.setGold(r.getGold() + eachGold);

            List<Goods> goods = new ArrayList<>();
            List<Equip> eList = new ArrayList<>();
            if (!equips.isEmpty()) {
                goods.add(new Goods(equips.get(0).getEquipId(), CommodityType.EQUIP.getCode()));
                eList.add(equips.get(0));
                equips.remove(0);
            }

            // 进行更新
            roleService.updateRoleInfo(r);
            if (!goods.isEmpty()) {
                equipService.addRoleEquips(r, goods);
            }

            // 消息回复
            Channel channel = ChannelCache.getUserIdChannelMap().get(r.getUserId());
            dup.setGoldReward(eachGold);
            dupResp = combineDupMsg(dup, eList);
            channel.writeAndFlush(dupResp);
        }
    }


    /**
     * 构造奖励回复信息
     *
     * @param dup       副本信息
     * @param equips    装备信息
     * @return
     */
    private MsgDuplicateProto.ResponseDuplicate combineDupMsg(Duplicate dup, List<Equip> equips){
        dupResp = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.DUPLICATE_CHALLENGE_SUCCESS)
                .setType(MsgDuplicateProto.RequestType.DUPLICATE)
                .addDuplicate(protoService.transToDuplicate(dup))
                .addAllEquip(protoService.transToEquipList(equips))
                .build();
        return dupResp;
    }

}
