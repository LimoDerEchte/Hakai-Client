package com.hakai.irc.protocol.codec;

import com.hakai.irc.client.IRCClient;
import com.hakai.irc.protocol.IRCIncomingPacket;
import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.io.IOException;
import java.util.List;

public class IRCPacketCodec extends ByteToMessageCodec<IRCOutgoingPacket> {

    private final IRCClient client;

    public IRCPacketCodec(IRCClient client) {
        this.client = client;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IRCOutgoingPacket packet, ByteBuf out) throws Exception {
        int id = client.getProtocol().getOutputId(packet);
        if(id < 0) {
            throw new IOException("Bad packet class " + packet.getClass().getName());
        } else {
            out.writeByte(id);
            packet.write(out);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            int id = in.readUnsignedByte();
            IRCIncomingPacket packet = client.getProtocol().createIncomingPacket(id);
            if(packet == null) {
                throw new IOException("Bad packet id " + id);
            } else {
                packet.read(in);
                out.add(packet);
            }
        }
    }

}
