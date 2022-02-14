package com.hakai.irc.protocol.packets.capes;

import com.hakai.irc.client.IRCPacketHandler;
import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCIncomingPacket;
import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;

public class IRCPacketGetAvailableCapes implements IRCIncomingPacket, IRCOutgoingPacket {
    private ArrayList<String> capes = new ArrayList<>();
    private String selected;

    @Override
    public void read(ByteBuf buf) throws IOException {
        String in = BufferUtils.readString(buf);
        for(String cape : in.split(",")){
            capes.add(cape);
        }
        selected = BufferUtils.readString(buf);
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
    }

    @Override
    public void handle(IRCPacketHandler handler) throws IOException {
        handler.handleGetAvailableCapes(this);
    }

    public ArrayList<String> getCapes() {
        return capes;
    }

    public String getSelected() {
        return selected;
    }
}
