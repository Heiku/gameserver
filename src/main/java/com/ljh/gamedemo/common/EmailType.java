package com.ljh.gamedemo.common;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 *
 *
 * 邮件的通用格式
 */
public enum  EmailType {

    BUY(1, "商品购买到货", "你在商城购买的商品已经到货，请领取邮件接收道具！"),
    FACE_TRANS(2, "面对面交易", "你与其他玩家面对面交易的物品到货，请领取邮件接收物品！")
    ;

    int type;
    String theme;
    String content;


    EmailType(int type, String theme, String content){
        this.type = type;
        this.theme = theme;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getTheme() {
        return theme;
    }
}
