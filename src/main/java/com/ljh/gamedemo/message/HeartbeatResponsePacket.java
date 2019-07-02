package com.ljh.gamedemo.message;

import lombok.Data;

@Data
public class HeartbeatResponsePacket extends Packet {

    @Override
    public Byte getCommand() {
        return Command.HEARTBEAT_RESPONSE;
    }
}
