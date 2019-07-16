package com.ljh.gamedemo.common;

/**
 * @Author: Heiku
 * @Date: 2019/7/16
 *
 * itemsType，物品分类
 */
public enum ItemsType {

    BLOOD_VIAL(101, "血瓶"),
    BLUE_VIAL(102, "蓝瓶");


    int code;
    String content;

    public static final int BLOOD = 1;
    public static final int BLUE = 2;


    ItemsType(int code, String content){
        this.code = code;
        this.content = content;
    }


    // 通过type code获取物品名称
    public String getContentFromCode(int code){
        for (ItemsType value : ItemsType.values()) {
            if (value.getCode() == code){
                return value.getContent();
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }
}
