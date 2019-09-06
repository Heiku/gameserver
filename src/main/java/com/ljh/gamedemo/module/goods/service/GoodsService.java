package com.ljh.gamedemo.module.goods.service;

import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.module.equip.bean.Equip;
import com.ljh.gamedemo.module.goods.bean.Goods;
import com.ljh.gamedemo.module.items.bean.Items;
import com.ljh.gamedemo.module.equip.service.EquipService;
import com.ljh.gamedemo.module.items.service.ItemService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.equip.local.LocalEquipMap;
import com.ljh.gamedemo.module.goods.local.LocalGoodsMap;
import com.ljh.gamedemo.module.items.local.LocalItemsMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 背包物品服务
 *
 * @Author: Heiku
 * @Date: 2019/8/22
 */

@Service
public class GoodsService {

    /**
     * 装备服务
     */
    @Autowired
    private EquipService equipService;

    /**
     * 物品服务
     */
    @Autowired
    private ItemService itemService;

    /**
     * 判断玩家是否拥有这些物品
     *
     * @param r         玩家信息
     * @param goodsId   物品id
     * @param num       数量
     * @return          返回是否拥有
     */
    public synchronized boolean containGoods(Role r, long goodsId, int num){
        // 判断物品id 是否存在
        Goods goods = LocalGoodsMap.getIdGoodsMap().get(goodsId);
        if (goods == null){
            return false;
        }

        // 判断玩家是否拥有这些物品
        int type = goods.getType();
        if (type == CommodityType.ITEM.getCode()){
            List<Items> itemsList = LocalItemsMap.getRoleItemsMap().get(r.getRoleId());

            // 玩家没有物品信息，直接返回
            if (itemsList == null || itemsList.isEmpty()){
                return false;
            }

            // 获取对应的物品信息
            Optional<Items> result = itemsList.stream().filter(items -> items.getItemsId() == goodsId).findFirst();
            if (!result.isPresent()){
                return false;
            }
            return result.get().getNum() >= num;
        }
        // 装备信息
        else if (type == CommodityType.EQUIP.getCode()){
            List<Equip> equipList = LocalEquipMap.getHasEquipMap().get(r.getRoleId());
            if (equipList == null || equipList.isEmpty()){
                return false;
            }

            Optional<Equip> result = equipList.stream().filter(e -> e.getEquipId() == goodsId).findFirst();
            if (!result.isPresent()){
                return false;
            }

            return num == 1;
        }
        return false;
    }


    /**
     * 获取交易商品的最低交易金额
     *
     * @param goodsId   物品id
     * @param num       数量
     * @return          返回
     */
    public int getGoodsMinTrans(long goodsId, int num) {
        // 获取物品信息
        Goods goods = LocalGoodsMap.getIdGoodsMap().get(goodsId);

        // 判断玩家是否拥有这些物品
        int type = goods.getType();
        if (type == CommodityType.ITEM.getCode()){
            // 获取信息
            Items items = LocalItemsMap.getIdItemsMap().get(goodsId);
            return items.getMinTrans() * num;
        }

        else if (type == CommodityType.EQUIP.getCode()){
            Equip equip = LocalEquipMap.getIdEquipMap().get(goodsId);
            return equip.getMinTrans() * num;
        }

        return 0;
    }


    /**
     * 移除玩家的物品信息
     *
     * @param roleId        玩家id
     * @param goodsId       物品id
     * @param num           数量
     */
    public void removeRoleGoods(long roleId, long goodsId, int num) {
        // 获取物品信息
        Goods goods = LocalGoodsMap.getIdGoodsMap().get(goodsId);
        Role r = LocalUserMap.getIdRoleMap().get(roleId);

        // 判断玩家是否拥有这些物品
        int type = goods.getType();
        if (type == CommodityType.ITEM.getCode()){
            // 更新物品信息
            itemService.updateRoleItems(r, goodsId, -num);
        }

        else if (type == CommodityType.EQUIP.getCode()){
            // 更新装备信息
            equipService.removeRoleEquips(r, goodsId, num);
        }
    }


    /**
     * 向玩家发放物品到背包
     *
     * @param r         玩家
     * @param goods     物品
     * @param num       数量
     */
    public void sendRoleGoods(Role r, Goods goods, int num){
        // 获取发放物品的状态
        int type = goods.getType();
        if (type == CommodityType.ITEM.getCode()){

            // 添加玩家的物品数量
            itemService.addRoleItems(r, goods.getGid(), num);
        }

        else if (type == CommodityType.EQUIP.getCode()){

            // 添加玩家的装备数量
            List<Goods> gList = new ArrayList<>();
            gList.add(goods);
            equipService.addRoleEquips(r, gList);
        }
    }


    /**
     * 获取出售物品的名称
     *
     * @param goodsId       物品id
     * @return              物品名
     */
    public String getGoodsName(Long goodsId) {
        Goods goods = LocalGoodsMap.getIdGoodsMap().get(goodsId);
        if (goods.getType().intValue() == CommodityType.ITEM.getCode()){
            return LocalItemsMap.getIdItemsMap().get(goodsId).getName();
        }else {
            return LocalEquipMap.getIdEquipMap().get(goodsId).getName();
        }
    }
}
