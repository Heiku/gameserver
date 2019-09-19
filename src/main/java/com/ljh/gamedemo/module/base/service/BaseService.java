package com.ljh.gamedemo.module.base.service;

import com.ljh.gamedemo.util.DateUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 基础服务
 */
@Service
public class BaseService {


    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;


    /**
     * 获取时间
     *
     * @param channel channel
     */
    public void getDate(Channel channel){
        String date = DateUtil.getCurrentDate();
        protoService.sendCommonMsg(channel, date);
    }


    /**
     * 发送心跳消息返回
     */
    public void sendHeartBeatResponse(){
        return;
    }
}
