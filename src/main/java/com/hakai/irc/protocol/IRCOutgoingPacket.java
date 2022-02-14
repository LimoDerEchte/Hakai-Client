package com.hakai.irc.protocol;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface IRCOutgoingPacket {

    void write(ByteBuf buf) throws IOException;

}
