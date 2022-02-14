package com.hakai.irc.protocol.packets.chat;

import com.hakai.irc.client.IRCPacketHandler;
import com.hakai.irc.protocol.IRCIncomingPacket;
import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;

public class IRCPacketKeepAlive implements IRCIncomingPacket, IRCOutgoingPacket {

    private int id;

    @Override
    public void read(ByteBuf buf) {
        id = buf.readUnsignedShort();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeShort(id);
    }

    @Override
    public void handle(IRCPacketHandler handler) {
        handler.handleKeepAlive(this);
    }

    public int getId() {
        return id;
    }
}
