package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.entity.Equip;
import com.ljh.gamedemo.entity.Goods;
import com.ljh.gamedemo.entity.Items;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalEquipMap;
import com.ljh.gamedemo.local.LocalGoodsMap;
import com.ljh.gamedemo.local.LocalItemsMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
