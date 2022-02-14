package com.hakai.irc.protocol.packets.mojang;

import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class IRCPacketMinecraftRequest implements IRCOutgoingPacket {

    private final String username;

    public IRCPacketMinecraftRequest(String username) {
        this.username = username;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeBoolean(username != null);
        if(username != null)
            BufferUtils.writeString(buf, username);
    }

}
