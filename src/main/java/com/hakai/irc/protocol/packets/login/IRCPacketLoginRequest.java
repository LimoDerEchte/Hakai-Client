package com.hakai.irc.protocol.packets.login;

import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;

public class IRCPacketLoginRequest implements IRCOutgoingPacket {

    private boolean register;
    private byte[] auth;

    public IRCPacketLoginRequest(boolean register, byte[] auth) {
        this.register = register;
        this.auth = auth;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBoolean(register);
        buf.writeShort(auth.length);
        buf.writeBytes(auth);
    }

}
