package com.hakai.irc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.List;

public class IRCPacketEncryption extends ByteToMessageCodec<ByteBuf> {

    private Cipher inCipher;
    private Cipher outCipher;

    private byte[] decryptedArray = new byte[0];
    private byte[] encryptedArray = new byte[0];

    public IRCPacketEncryption(SecretKey sharedKey) throws GeneralSecurityException  {
        this.inCipher = Cipher.getInstance("AES/CFB8/NoPadding");
        this.inCipher.init(Cipher.DECRYPT_MODE, sharedKey, new IvParameterSpec(sharedKey.getEncoded()));
        this.outCipher = Cipher.getInstance("AES/CFB8/NoPadding");
        this.outCipher.init(Cipher.ENCRYPT_MODE, sharedKey, new IvParameterSpec(sharedKey.getEncoded()));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int length = in.readableBytes();
        byte[] bytes = getBytes(in, length);
        int outLength = outCipher.getOutputSize(length);
        if(encryptedArray.length < outLength) {
            encryptedArray = new byte[outLength];
        }
        out.writeBytes(encryptedArray, 0, outCipher.update(bytes, 0, length, encryptedArray, 0));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readableBytes();
        byte[] bytes = getBytes(in, length);
        ByteBuf result = ctx.alloc().heapBuffer(inCipher.getOutputSize(length));
        result.writerIndex(inCipher.update(bytes, 0, length, result.array(), result.arrayOffset()));
        out.add(result);
    }

    private byte[] getBytes(ByteBuf buf, int i) {
        if(this.decryptedArray.length < i) {
            this.decryptedArray = new byte[i];
        }

        buf.readBytes(this.decryptedArray, 0, i);
        return this.decryptedArray;
    }

}
