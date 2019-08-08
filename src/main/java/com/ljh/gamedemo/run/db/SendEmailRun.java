package com.ljh.gamedemo.run.db;

import com.ljh.gamedemo.entity.Commodity;
import com.ljh.gamedemo.entity.Email;
import com.ljh.gamedemo.service.ProtoService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 */

@Slf4j
public class SendEmailRun implements Runnable {

    private Channel channel;

    private Email email;

    private ProtoService protoService = ProtoService.getInstance();

    @Override
    public void run() {

    }
}
