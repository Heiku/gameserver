package com.ljh.gamedemo.module.task.service;

import com.ljh.gamedemo.proto.protoc.MsgTaskProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 任务的具体操作
 *
 * @Author: Heiku
 * @Date: 2019/9/6
 */

@Service
@Slf4j
public class TaskService {
    public void taskAll(MsgTaskProto.RequestTask req, Channel channel) {
    }

    public void taskState(MsgTaskProto.RequestTask req, Channel channel) {
    }

    public void taskReceive(MsgTaskProto.RequestTask req, Channel channel) {
    }

    public void taskGiveUp(MsgTaskProto.RequestTask req, Channel channel) {
    }
}
