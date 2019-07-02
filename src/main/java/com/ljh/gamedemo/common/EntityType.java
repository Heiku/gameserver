package com.ljh.gamedemo.common;

public enum  EntityType {
    Knight(100, "骑士"),
    Warrior(101, "战士"),
    Mage(102, "法师"),
    Thief(103, "盗贼"),
    Villager(104, "村民"),
    Guard(105, "守卫"),
    Trader(106, "商人"),
    Creeps(107, "野怪"),
    Robber(108, "强盗"),
    Beast(109, "野兽"),
    Orc(110, "兽人"),
    Devil(111, "恶魔"),
    HUMAN(112, "人类"),
    NONE(113, "虫子");

    int code;
    String content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    EntityType(int code, String content){
        this.code = code;
        this.content = content;
    }

    public static EntityType getContentFromCode(int code){
        for (EntityType entityType : EntityType.values()){
            if (entityType.code == code){
                return entityType;
            }
        }
        return EntityType.NONE;
    }
}
