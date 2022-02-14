package com.hakai.irc.protocol.packets.login;

import com.hakai.irc.client.IRCPacketHandler;
import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCIncomingPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.text.Text;

import java.io.IOException;

public class IRCPacketLogout implements IRCIncomingPacket {

    private boolean resetToken;
    private Text reason;

    @Override
    public void read(ByteBuf buf) throws IOException {
        this.resetToken = buf.readBoolean();
        this.reason = Text.Serializer.fromJson(BufferUtils.readString(buf));
    }

    @Override
    public void handle(IRCPacketHandler handler) {
        handler.handleLogout(this);
    }

    public boolean isResetToken() {
        return resetToken;
    }

    public Text getReason() {
        return reason;
    }

}
