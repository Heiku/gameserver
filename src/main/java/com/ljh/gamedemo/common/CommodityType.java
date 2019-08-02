package com.ljh.gamedemo.common;

/**
 * @Author: Heiku
 * @Date: 2019/8/2
 *
 * 商品类型
 */
public enum  CommodityType {

    ITEM(1, "物品"),
    EQUIP(2, "装备")

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
