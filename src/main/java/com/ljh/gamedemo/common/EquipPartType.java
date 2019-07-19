package com.ljh.gamedemo.common;

/**
 * @Author: Heiku
 * @Date: 2019/7/18
 */
public enum EquipPartType {

    HELMET(1, "帽子"),
    MASK(2, "面具"),
    NECKLACE(3, "项链"),
    CLOTHES(4, "衣服"),
    PANTS(5, "裤子"),
    ARM_GUARD(6, "护臂"),
    LEGGINGS(7, "护腿"),
    RING(8, "戒指"),
    SHOES(9, "鞋子"),

    WEAPONS(10, "武器");


    int partId;
    String content;

    EquipPartType(int partId, String content){
        this.partId = partId;
        this.content = content;
    }


    public String getContent() {
        return content;
    }

    public int getPartId() {
        return partId;
    }
}
