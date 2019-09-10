package com.ljh.gamedemo.common;

/**
 * 邮件的通用格式
 *
 * @Author: Heiku
 * @Date: 2019/8/8
 */
public enum  EmailType {

    BUY(1, "商品购买到货", "你在商城购买的商品已经到货，请领取邮件接收道具！"),
    FACE_TRANS(2, "面对面交易", "你与其他玩家面对面交易的物品到货，请领取邮件接收物品！"),
    GUILD_TAKE_OUT(3, "公会物品", "你在公会领取的物品已经到货，请领取邮件接收物品！"),
    TRADE_AUCTION(4, "交易行拍卖物品", "你在拍卖行购买的物品已经到货，请领取邮件接收物品！"),
    TASK_REWARD(5, "任务完成奖励", "恭喜你完成任务，请领取邮件接收物品！")
    ;

    int type;
    String theme;
    String content;


    EmailType(int _type, String _theme, String _content){
        this.type = _type;
        this.theme = _theme;
        this.content = _content;
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
