package com.hakai.irc.protocol.packets.capes;

import com.hakai.irc.client.IRCPacketHandler;
import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCIncomingPacket;
import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class IRCPacketGetCape implements IRCIncomingPacket, IRCOutgoingPacket {

    private UUID uuid;
    private String capeId;
    private String capeUrl;
    private String filename;
    private int animationSpeed;

    public IRCPacketGetCape() {}

    public IRCPacketGetCape(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        uuid = UUID.fromString(BufferUtils.readString(buf));
        capeId = BufferUtils.readString(buf);
        capeUrl = BufferUtils.readString(buf);
        filename = BufferUtils.readString(buf);
        animationSpeed = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        BufferUtils.writeString(buf, uuid.toString());
    }

    @Override
    public void handle(IRCPacketHandler handler) throws IOException {
        handler.handleGetCape(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCapeId() {
        return capeId;
    }

    public String getCapeUrl() {
        return capeUrl;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public String getFilename() {
        return filename;
    }
}
