package com.ljh.gamedemo.server.codec.local;

import com.google.protobuf.Message;
import com.ljh.gamedemo.server.codec.MessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/18
 */

public class LocalMessageMap {

    public static Map<Class<Message>, Integer> messageMap = new HashMap<>();


    public static void readAllMessageType(){
        for (MessageType messageType : MessageType.values()) {
            messageMap.put(messageType.getMessageLite(), messageType.getProtoCode());
        }
    }

    public static void main(String[] args) {

        readAllMessageType();

        messageMap.forEach((k, v) -> {
            System.out.println("k: " + k + " v: " + v);
        });
    }
}
