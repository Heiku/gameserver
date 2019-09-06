package com.ljh.gamedemo.common;

/**
 * 商品类型
 *
 * @Author: Heiku
 * @Date: 2019/8/2
 */
public enum  CommodityType {

    ITEM(1, "物品"),
    EQUIP(2, "装备"),
    GOLD(3, "金币")
    ;


    private Integer code;
    private String content;

    CommodityType(Integer code, String content) {
        this.code = code;
        this.content = content;
    }

    public static String getContentFromCode(int code){
        for (CommodityType v : CommodityType.values()) {
            if (v.getCode() == code){
                return v.getContent();
            }
        }
        return null;
    }


    public String getContent() {
        return content;
    }

    public Integer getCode() {
        return code;
    }
}
