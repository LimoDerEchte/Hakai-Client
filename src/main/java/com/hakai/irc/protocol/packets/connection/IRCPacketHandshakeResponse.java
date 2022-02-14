package com.hakai.irc.protocol.packets.connection;

import com.hakai.irc.client.IRCPacketHandler;
import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCIncomingPacket;
import io.netty.buffer.ByteBuf;

public class IRCPacketHandshakeResponse implements IRCIncomingPacket {

    private boolean success;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    @Override
    public void read(ByteBuf buf) {
        success = buf.readBoolean();
        if(success)
            error = null;
        else
            error = BufferUtils.readString(buf);
    }

    @Override
    public void handle(IRCPacketHandler handler) {
        handler.handleHandshakeResponse(this);
    }

}
