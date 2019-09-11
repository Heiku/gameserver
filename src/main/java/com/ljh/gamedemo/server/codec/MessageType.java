package com.ljh.gamedemo.server.codec;

import com.google.protobuf.MessageLite;
import com.ljh.gamedemo.proto.protoc.*;

import java.lang.reflect.Constructor;


/**
 * 协议信息
 */
public enum MessageType {

    MESSAGE_PROTO(0x01, MessageBase.Message.class),

    // user
    REQUEST_USERINFO_PROTO(0x02, MsgUserInfoProto.RequestUserInfo.class),
    RESPONSE_USERINFO_PROTO(0x03, MsgUserInfoProto.ResponseUserInfo.class),

    // site
    REQUEST_SITE_PROTO(0x04, MsgSiteInfoProto.RequestSiteInfo.class),
    RESPONSE_SITE_PROTO(0x05, MsgSiteInfoProto.ResponseSiteInfo.class),

    // entity
    REQUEST_ENTITY_PROTO(0x06, MsgEntityInfoProto.RequestEntityInfo.class),
    RESPONSE_ENTITY_PROTO(0x07, MsgEntityInfoProto.ResponseEntityInfo.class),

    // talk entity
    REQUEST_TALK_ENTITY(0x08, TalkEntityProto.RequestTalkEntity.class),
    RESPONSE_TALK_ENTITY(0x09,TalkEntityProto.ResponseTalkEntity.class),

    // spell
    REQUEST_SPELL(0xa, MsgSpellProto.RequestSpell.class),
    RESPONSE_SPELL(0xb, MsgSpellProto.ResponseSpell.class),

    // attack spell
    REQUEST_ATTACK_SPELL(0xc, MsgAttackCreepProto.RequestAttackCreep.class),
    RESPONSE_ATTACK_SPELL(0xd, MsgAttackCreepProto.ResponseAttackCreep.class),

    // items
    REQUEST_ITEMS(0xe, MsgItemProto.RequestItem.class),
    RESPONSE_ITEMS(0xf, MsgItemProto.ResponseItem.class),

    REQUEST_EQUIPS(0x10, MsgEquipProto.RequestEquip.class),
    RESPONSE_EQUIPS(0x11, MsgEquipProto.ResponseEquip.class),

    // duplicate
    REQUEST_DUPLICATE(0x12, MsgDuplicateProto.RequestDuplicate.class),
    RESPONSE_DUPLICATE(0x13, MsgDuplicateProto.ResponseDuplicate.class),

    // mall
    REQUEST_MALL(0x14, MsgMallProto.RequestMall.class),
    RESPONSE_MALL(0x15, MsgMallProto.ResponseMall.class),

    // chat
    REQUEST_CHAT(0x16, MsgChatProto.RequestChat.class),
    RESPONSE_CHAT(0x17, MsgChatProto.ResponseChat.class),

    // email
    REQUEST_EMAIL(0x18, MsgEmailProto.RequestEmail.class),
    RESPONSE_EAMIL(0x19, MsgEmailProto.ResponseEmail.class),

    // pk
    REQUEST_PK(0x1a, MsgPKProto.RequestPK.class),
    RESPONSE_PK(0x1b, MsgPKProto.ResponsePK.class),

    // group
    REQUEST_GROUP(0x1c, MsgGroupProto.RequestGroup.class),
    RESPONSE_GROUP(0x1d, MsgGroupProto.ResponseGroup.class),

    // role
    REQUEST_ROLE(0x1e, MsgRoleProto.RequestRole.class),
    RESPONSE_ROLE(0x1f, MsgRoleProto.ResponseRole.class),

    // face_transaction
    REQUEST_FACE_TRANS(0x20, MsgFaceTransProto.RequestFaceTrans.class),
    RESPONSE_FACE_TRANS(0x21, MsgFaceTransProto.ResponseFaceTrans.class),

    // guild
    REQUEST_GUILD(0x22,MsgGuildProto.RequestGuild.class),
    RESPONSE_GUILD(0x23, MsgGuildProto.ResponseGuild.class),

    // trade
    REQUEST_TRADE(0x24, MsgTradeProto.RequestTrade.class),
    RESPONSE_TRADE(0x25, MsgTradeProto.ResponseTrade.class),

    // task
    REQUEST_TASK(0x26, MsgTaskProto.RequestTask.class),
    RESPONSE_TASK(0x27, MsgTaskProto.ResponseTask.class)
    ;


    public Integer protoCode;
    public Class messageLite;

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

                // 反射生成协议对象C
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
