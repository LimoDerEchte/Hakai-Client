package com.hakai.irc.protocol;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class BufferUtils {

    public static String readString(ByteBuf buffer) {
        int length = buffer.readUnsignedShort();
        String string = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
        buffer.readerIndex(buffer.readerIndex() + length);
        return string;
    }

    public static void writeString(ByteBuf buffer, String string) {
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(bs.length);
        buffer.writeBytes(bs);
    }

    public static byte[] readBytes(ByteBuf buf, int length) {
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return bytes;
    }

}
