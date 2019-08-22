package com.ljh.gamedemo.server.request;

/**
 * 面对面交易的请求类型
 *
 * @Author: Heiku
 * @Date: 2019/8/22
 */
public class RequestFaceTransType {

    /**
     * 交易发起
     */
    public static final int INITIATE = 0;

    /**
     * 接收申请交易
     */
    public static final int YES_TRANS = 1;

    /**
     * 拒绝申请交易
     */
    public static final int NO_TRANS = 2;

    /**
     * 发送交易内容
     */
    public static final int ASK_TRANS = 3;

    /**
     * 接受交易结果
     */
    public static final int ACCEPT_TRANS = 4;

    /**
     * 拒绝交易结果
     */
    public static final int REFUSE_TRANS = 5;

    /**
     * 离开交易
     */
    public static final int LEAVE_TRANS = 6;

}
