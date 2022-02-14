package com.hakai.irc.protocol.packets.capes;

import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class IRCPacketUpdateCape implements IRCOutgoingPacket {
    private String capeId;

    public IRCPacketUpdateCape(){};

    public IRCPacketUpdateCape(String capeId){
        this.capeId = capeId;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        BufferUtils.writeString(buf, capeId);
    }
}
