package com.ljh.gamedemo.message;

import lombok.Data;

@Data
public class HeartbeatRequestPacket extends Packet {

    @Override
    public Byte getCommand() {
        return Command.HEARTBEAT_REQUEST;
    }
}
