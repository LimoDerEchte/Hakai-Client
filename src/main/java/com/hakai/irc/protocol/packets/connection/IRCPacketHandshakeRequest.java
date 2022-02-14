package com.hakai.irc.protocol.packets.connection;

import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;

public class IRCPacketHandshakeRequest implements IRCOutgoingPacket {

    private final int ircVersion;

    public IRCPacketHandshakeRequest(int ircVersion) {
        this.ircVersion = ircVersion;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(ircVersion);
    }

}
