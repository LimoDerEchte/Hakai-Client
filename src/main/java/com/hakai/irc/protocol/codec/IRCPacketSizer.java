package com.hakai.irc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class IRCPacketSizer extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int i = in.readableBytes();
        out.ensureWritable(2 + i);
        out.writeShort(i);
        out.writeBytes(in, in.readerIndex(), i);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> outList) throws Exception {
        in.markReaderIndex();
        if(in.readableBytes() >= 2) {
            int i = in.readUnsignedShort();
            if(in.readableBytes() < i) {
                in.resetReaderIndex();
                return;
            }
            outList.add(in.readBytes(i));
        }
    }

}
