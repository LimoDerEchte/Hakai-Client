package com.hakai.irc.protocol.packets.chat;

import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;

public class IRCPacketMessageSend implements IRCOutgoingPacket {

    private String message;

    public IRCPacketMessageSend(String message) {
        this.message = message;
    }

    @Override
    public void write(ByteBuf buf) {
        BufferUtils.writeString(buf, message);
    }

}
