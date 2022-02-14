package com.hakai.irc.protocol.packets.encrypt;

import com.hakai.irc.protocol.IRCOutgoingPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import org.apache.logging.log4j.LogManager;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;

public class IRCPacketEncryptResponse implements IRCOutgoingPacket {

    private byte[] encryptedVerify;
    private byte[] encryptedKey;

    public IRCPacketEncryptResponse(PublicKey publicKey, byte[] verify, SecretKey secretKey) throws NetworkEncryptionException {
        this.encryptedVerify = NetworkEncryptionUtils.encrypt(publicKey, verify);
        LogManager.getLogger().info(Arrays.toString(secretKey.getEncoded()));
        this.encryptedKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeShort(encryptedVerify.length);
        buf.writeBytes(encryptedVerify);
        buf.writeShort(encryptedKey.length);
        buf.writeBytes(encryptedKey);
    }

}
