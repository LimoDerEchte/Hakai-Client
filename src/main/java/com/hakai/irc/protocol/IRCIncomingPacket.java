package com.hakai.irc.protocol;

import com.hakai.irc.client.IRCPacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface IRCIncomingPacket {

    void read(ByteBuf buf) throws IOException;

    void handle(IRCPacketHandler handler) throws IOException;

}
