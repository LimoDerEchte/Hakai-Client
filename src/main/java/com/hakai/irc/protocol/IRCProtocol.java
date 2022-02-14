package com.hakai.irc.protocol;

import com.hakai.irc.protocol.packets.capes.IRCPacketGetAvailableCapes;
import com.hakai.irc.protocol.packets.capes.IRCPacketGetCape;
import com.hakai.irc.protocol.packets.capes.IRCPacketUpdateCape;
import com.hakai.irc.protocol.packets.chat.IRCPacketKeepAlive;
import com.hakai.irc.protocol.packets.chat.IRCPacketMessageReceive;
import com.hakai.irc.protocol.packets.chat.IRCPacketMessageSend;
import com.hakai.irc.protocol.packets.connection.IRCPacketHandshakeRequest;
import com.hakai.irc.protocol.packets.connection.IRCPacketHandshakeResponse;
import com.hakai.irc.protocol.packets.encrypt.IRCPacketEncryptRequest;
import com.hakai.irc.protocol.packets.encrypt.IRCPacketEncryptResponse;
import com.hakai.irc.protocol.packets.login.IRCPacketLoginRequest;
import com.hakai.irc.protocol.packets.login.IRCPacketLoginSuccess;
import com.hakai.irc.protocol.packets.login.IRCPacketLogout;
import com.hakai.irc.protocol.packets.mojang.IRCPacketMinecraftRequest;

public class IRCProtocol {

    public IRCIncomingPacket createIncomingPacket(int id) {
        switch (id) {
            case 0:
                return new IRCPacketKeepAlive();
            case 1:
                return new IRCPacketEncryptRequest();
            case 2:
                return new IRCPacketHandshakeResponse();
            case 3:
                return new IRCPacketLoginSuccess();
            case 4:
                return new IRCPacketLogout();
            case 5:
                return new IRCPacketMessageReceive();
            case 6:
                return new IRCPacketGetCape();
            case 7:
                return new IRCPacketGetAvailableCapes();
        }
        return null;
    }

    public int getOutputId(IRCOutgoingPacket packet) {
        if(packet instanceof IRCPacketKeepAlive)
            return 0;
        else if(packet instanceof IRCPacketEncryptResponse)
            return 1;
        else if(packet instanceof IRCPacketHandshakeRequest)
            return 2;
        else if(packet instanceof IRCPacketLoginRequest)
            return 3;
        else if(packet instanceof IRCPacketMessageSend)
            return 5;
        else if(packet instanceof IRCPacketGetCape)
            return 6;
        else if(packet instanceof IRCPacketGetAvailableCapes)
            return 7;
        else if(packet instanceof IRCPacketUpdateCape)
            return 8;
        else if(packet instanceof IRCPacketMinecraftRequest)
            return 9;
        return -1;
    }

}
