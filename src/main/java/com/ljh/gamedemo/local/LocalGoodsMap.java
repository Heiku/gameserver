package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Goods;

import java.util.Map;

/**
 * 存储玩家的物品数据
 *
 * @Author: Heiku
 * @Date: 2019/8/7
 */
public class LocalGoodsMap {

    /**
     * 玩家的物品数据map <gid, Goods>
     */
    private static Map<Long, Goods> idGoodsMap = Maps.newConcurrentMap();


    public static Map<Long, Goods> getIdGoodsMap() {
        return idGoodsMap;
    }
}
