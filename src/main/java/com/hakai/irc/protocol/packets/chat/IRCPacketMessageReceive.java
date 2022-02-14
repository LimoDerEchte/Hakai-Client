package com.hakai.irc.protocol.packets.chat;

import com.hakai.irc.client.IRCPacketHandler;
import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCIncomingPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.text.Text;

public class IRCPacketMessageReceive implements IRCIncomingPacket {

    private Text username;
    private Text message;

    @Override
    public void read(ByteBuf buf) {
        username = Text.Serializer.fromJson(BufferUtils.readString(buf));
        message = Text.Serializer.fromJson(BufferUtils.readString(buf));
    }

    public Text getUsername() {
        return username;
    }

    public Text getMessage() {
        return message;
    }

    @Override
    public void handle(IRCPacketHandler handler) {
        handler.handleMessageReceived(this);
    }

}
