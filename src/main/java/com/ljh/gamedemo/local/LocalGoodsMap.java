package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Goods;

import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/8/7
 */
public class LocalGoodsMap {

    private static Map<Long, Goods> idGoodsMap = Maps.newConcurrentMap();


    public static Map<Long, Goods> getIdGoodsMap() {
        return idGoodsMap;
    }
}
