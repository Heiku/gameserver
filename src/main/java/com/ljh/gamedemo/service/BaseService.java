package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.proto.protoc.MessageBase;
import com.ljh.gamedemo.util.DateUtil;
import org.springframework.stereotype.Service;

@Service
public class BaseService {

    public MessageBase.Message getDate(){
        String date = DateUtil.getCurrentDate();

        return MessageBase.Message.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(date)
                .build();
    }


    public MessageBase.Message sendHeartBeatResponse(){
        return MessageBase.Message.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .build();
    }
}
