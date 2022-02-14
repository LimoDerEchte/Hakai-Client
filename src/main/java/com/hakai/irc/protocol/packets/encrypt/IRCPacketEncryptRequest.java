package com.hakai.irc.protocol.packets.encrypt;

import com.hakai.irc.client.IRCPacketHandler;
import com.hakai.irc.protocol.BufferUtils;
import com.hakai.irc.protocol.IRCIncomingPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;

import java.io.IOException;
import java.security.PublicKey;

public class IRCPacketEncryptRequest implements IRCIncomingPacket {

    private byte[] verify;
    private PublicKey publicKey;

    @Override
    public void read(ByteBuf buf) throws IOException {
        byte[] publicKeyArray = BufferUtils.readBytes(buf, buf.readUnsignedShort());
        this.verify = BufferUtils.readBytes(buf, 4);

        try {
            this.publicKey = NetworkEncryptionUtils.readEncodedPublicKey(publicKeyArray);
        } catch (NetworkEncryptionException e) {
            throw new IOException("Could not decode public key.", e);
        }
    }

    @Override
    public void handle(IRCPacketHandler handler) throws IOException {
        handler.handleEncryption(this);
    }

    public byte[] getVerify() {
        return verify;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

}
