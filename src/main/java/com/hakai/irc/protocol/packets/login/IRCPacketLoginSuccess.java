package com.hakai.irc.protocol.packets.login;

import com.hakai.irc.client.IRCPacketHandler;
import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCIncomingPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.text.Text;

import java.io.IOException;

public class IRCPacketLoginSuccess implements IRCIncomingPacket {

    private Text username;
    private byte[] token;

    @Override
    public void read(ByteBuf buf) throws IOException {
        username = Text.Serializer.fromJson(BufferUtils.readString(buf));
        if(buf.readBoolean())
            token = BufferUtils.readBytes(buf, buf.readUnsignedShort());
        else
            token = null;
    }

    @Override
    public void handle(IRCPacketHandler handler) throws IOException {
        handler.handleLoginSuccess(this);
    }

    public Text getUsername() {
        return username;
    }

    public byte[] getToken() {
        return token;
    }
}
