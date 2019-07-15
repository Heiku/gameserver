package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/15
 */

@Service
public class DeathCreepService {

    private ProtoService protoService = new ProtoService();


    // 野怪死亡，更新缓存数据，发送消息返回
    public void deathCreep(Channel channel, Integer startUp){
        Map<Channel, Creep> channelCreepMap = LocalAttackCreepMap.getChannelCreepMap();
        Creep creep = channelCreepMap.get(channel);
        creep.setNum(creep.getNum() - 1);
        creep.setHp(startUp);
        channelCreepMap.put(channel, creep);

        LocalAttackCreepMap.setChannelCreepMap(channelCreepMap);

        MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setType(MsgAttackCreepProto.RequestType.ATTACK)
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ATTACK_DEATH_CREEP)
                .setCreep(protoService.transToCreep(creep))
                .build();

        channel.writeAndFlush(response);
    }

}
