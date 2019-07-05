package com.ljh.gamedemo.server.codec;

import com.google.protobuf.MessageLite;
import com.ljh.gamedemo.proto.MessageBase;
import com.ljh.gamedemo.proto.MsgEntityInfoProto;
import com.ljh.gamedemo.proto.MsgSiteInfoProto;
import com.ljh.gamedemo.proto.MsgUserInfoProto;

import java.lang.reflect.Constructor;

public enum MessageType {

    MESSAGE_PROTO(0x01, MessageBase.Message.class),

    // user
    REQUEST_USERINFO_PROTO(0x02, MsgUserInfoProto.RequestUserInfo.class),
    RESPONSE_USERINFO_PROTO(0x03, MsgUserInfoProto.ResponseUserInfo.class),

    // site
    REQUEST_SITE_PROTO(0x04, MsgSiteInfoProto.RequestSiteInfo.class),
    RESPONSE_SITE_PROTO(0x05, MsgSiteInfoProto.ResponseSiteInfo.class),

    // entity
    REQUEST_ENTITY_PROTO(0x06,MsgEntityInfoProto.RequestEntityInfo.class),
    RESPONSE_ENTITY_PROTO(0x07, MsgEntityInfoProto.ResponseEntityInfo.class),
    ;


    Integer protoCode;
    Class messageLite;

    MessageType(Integer protoCode, Class messageLite){
        this.protoCode = protoCode;
        this.messageLite = messageLite;
    }


    public Integer getProtoCode() {
        return protoCode;
    }

    public Class getMessageLite() {
        return messageLite;
    }

    /**
     * 通过协议序号 -> 协议对象
     *
     * @param code
     * @return
     */
    public static Object getProtoInstanceByCode(Integer code) throws Exception{
        for (MessageType protoType : MessageType.values()) {
            if (protoType.getProtoCode().intValue() == code){

                // 反射生成协议对象
                Constructor constructor = protoType.getMessageLite().getDeclaredConstructor();
                constructor.setAccessible(true);

                return constructor.newInstance();
            }
        }
        return null;
    }

    /**
     * 类对象 -> 序号
     *
     * @param messageLite
     * @return
     */
    public static Byte getProtoCodeFromType(MessageLite messageLite){
        for (MessageType protoType : MessageType.values()) {
            if (messageLite.getClass() == protoType.getMessageLite()){
                return Byte.valueOf(String.valueOf(protoType.getProtoCode()));
            }
        }
        return null;
    }
}
