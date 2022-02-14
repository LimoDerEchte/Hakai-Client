package com.hakai.irc.client;

import com.hakai.commands.CapeCommand;
import com.hakai.irc.protocol.packets.capes.IRCPacketGetAvailableCapes;
import com.hakai.irc.protocol.packets.capes.IRCPacketGetCape;
import com.hakai.irc.protocol.packets.capes.IRCPacketUpdateCape;
import com.hakai.irc.protocol.packets.chat.IRCPacketKeepAlive;
import com.hakai.irc.protocol.packets.chat.IRCPacketMessageReceive;
import com.hakai.irc.protocol.packets.connection.IRCPacketHandshakeRequest;
import com.hakai.irc.protocol.packets.connection.IRCPacketHandshakeResponse;
import com.hakai.irc.protocol.packets.encrypt.IRCPacketEncryptRequest;
import com.hakai.irc.protocol.packets.encrypt.IRCPacketEncryptResponse;
import com.hakai.irc.protocol.packets.login.IRCPacketLoginRequest;
import com.hakai.irc.protocol.packets.login.IRCPacketLoginSuccess;
import com.hakai.irc.protocol.packets.login.IRCPacketLogout;
import com.hakai.utils.CapeManager;
import com.hakai.utils.MessageUtils;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class IRCPacketHandler {

    private IRCClient client;

    private boolean connected = false;
    private boolean loggedIn = false;

    public IRCPacketHandler(IRCClient client) {
        this.client = client;
    }

    public void onConnected() {
        client.sendPacket(new IRCPacketHandshakeRequest(IRCClient.VERSION));
    }

    public void onDisconnected() {
        MessageUtils.printChatMessageWithPrefix("IRC disconnected.");
        connected = false;
        loggedIn = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void handleKeepAlive(IRCPacketKeepAlive packet) {
        client.sendPacket(packet);
    }

    public void handleHandshakeResponse(IRCPacketHandshakeResponse packet) {
        if(packet.isSuccess()) {
            MessageUtils.printChatMessageWithPrefix("IRCClient is connected");
            if(client.getConfig().getToken() != null) {
                client.sendPacket(new IRCPacketLoginRequest(false, client.getConfig().getToken()));
            }
            connected = true;
            client.updateLastMinecraftSession();
        } else {
            client.disconnect();
            MessageUtils.printChatMessageWithPrefix("IRC can't connect. Reason: " + packet.getError());
        }
    }

    public void handleMessageReceived(IRCPacketMessageReceive packet) {
        if(!client.getConfig().isChatEnabled())
            return;
        MessageUtils.printChatMessage(
                new LiteralText("IRC").formatted(Formatting.GOLD, Formatting.BOLD),
                new LiteralText(" \u00BB ").formatted(Formatting.DARK_GRAY, Formatting.BOLD),
                packet.getUsername(),
                new LiteralText(" \u00BB ").formatted(Formatting.DARK_GRAY, Formatting.BOLD),
                packet.getMessage()
        );
    }

    public void handleLogout(IRCPacketLogout packet) {
        loggedIn = false;
        if(packet.isResetToken())
            client.getConfig().setToken(null);

        MessageUtils.printChatMessageWithPrefix(
                new LiteralText("IRC").formatted(Formatting.GOLD, Formatting.BOLD),
                new LiteralText(" \u00BB ").formatted(Formatting.DARK_GRAY, Formatting.BOLD),
                new LiteralText("Logout: ").formatted(Formatting.RED),
                packet.getReason()
        );
    }

    public void handleLoginSuccess(IRCPacketLoginSuccess packet) {
        if(packet.getToken() != null)
            client.getConfig().setToken(packet.getToken());
        loggedIn = true;
        MessageUtils.printChatMessageWithPrefix(
                new LiteralText("IRC").formatted(Formatting.GOLD, Formatting.BOLD),
                new LiteralText(" \u00BB ").formatted(Formatting.DARK_GRAY, Formatting.BOLD),
                new LiteralText("Logged in ").formatted(Formatting.GREEN),
                packet.getUsername()
        );
    }

    public void handleEncryption(IRCPacketEncryptRequest packet) throws IOException {
        try {
            SecretKey secretKey = NetworkEncryptionUtils.generateKey();
            client.sendPacket(new IRCPacketEncryptResponse(packet.getPublicKey(), packet.getVerify(), secretKey));
            client.encrypt(secretKey);
        } catch (NetworkEncryptionException | GeneralSecurityException e) {
            throw new IOException("Failed to create and send SecretKey for IRC", e);
        }
    }

    public void handleGetCape(IRCPacketGetCape packet) {
        CapeManager.handleCapePacket(packet.getUuid(), packet.getCapeId(), packet.getCapeUrl(), packet.getFilename(), packet.getAnimationSpeed());
    }

    public void handleGetAvailableCapes(IRCPacketGetAvailableCapes packet){
        if(!CapeCommand.changeCape.equals("")){
            if(packet.getCapes().contains(CapeCommand.changeCape)) {
                IRCClient.get().sendPacket(new IRCPacketUpdateCape(CapeCommand.changeCape));
                CapeManager.updateAllCapes();
                MessageUtils.printChatMessageWithPrefix("§aCape wurde geändert.");
            } else {
                MessageUtils.printChatMessageWithPrefix("§cDu besitzt dieses Cape nicht!");
            }
            CapeCommand.changeCape = "";
            return;
        }

        MessageUtils.printChatMessageWithPrefix("§aDeine Capes:");
        for(String capeName : packet.getCapes()){
            if(capeName.equals(packet.getSelected())){
                MessageUtils.printChatMessageWithPrefix("§8> §6§l" + capeName);
            }else
                MessageUtils.printChatMessageWithPrefix("§6" + capeName);
        }
    }
}
