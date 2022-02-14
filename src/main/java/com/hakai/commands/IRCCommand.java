package com.hakai.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.hakai.command.Command;
import com.hakai.irc.callback.CallbackServer;
import com.hakai.irc.client.IRCClient;
import com.hakai.irc.protocol.packets.chat.IRCPacketMessageSend;
import com.hakai.irc.protocol.packets.login.IRCPacketLoginRequest;
import com.hakai.utils.MessageUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class IRCCommand extends Command {

    private final static SimpleCommandExceptionType IRC_DISABLED = new SimpleCommandExceptionType(new LiteralText("IRCChat is disabled"));
    private final static SimpleCommandExceptionType IRC_NOT_LOGGED_IN = new SimpleCommandExceptionType(new LiteralText("IRCChat must be logged in"));
    private final static SimpleCommandExceptionType IRC_ALREADY_LOGGED_IN = new SimpleCommandExceptionType(new LiteralText("IRCChat is already logged in"));
    private final static DynamicCommandExceptionType IRC_DYNAMIC_EXCEPTION = new DynamicCommandExceptionType((object) -> {
        return new LiteralText(((Exception) object).getMessage());
    });

    public IRCCommand() {
        super("irc", "toggle|login|reconnect|<message>");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("toggle").executes(context -> {
            boolean chatEnabled = !IRCClient.get().getConfig().isChatEnabled();
            IRCClient.get().getConfig().setChatEnabled(chatEnabled);
            if (chatEnabled) {
                MessageUtils.printChatMessageWithPrefix("&aIRC Chat now active");
            } else {
                MessageUtils.printChatMessageWithPrefix("&aIRC Chat now &cinactive");
            }
            return SINGLE_SUCCESS;
        })).then(literal("login").executes(context -> {
            if (!IRCClient.get().isConnected())
                throw IRC_DISABLED.create();
            if (IRCClient.get().getConfig().getToken() != null)
                throw IRC_ALREADY_LOGGED_IN.create();

            try {
                new CallbackServer((token) -> {
                    IRCClient.get().sendPacket(new IRCPacketLoginRequest(true, token.getBytes(StandardCharsets.UTF_8)));
                });
                Util.getOperatingSystem().open("https://discord.com/api/oauth2/authorize?client_id=903981360492990536&redirect_uri=http%3A%2F%2Flocalhost%3A5756&response_type=code&scope=identify%20guilds");
            } catch (IOException e) {
                throw IRC_DYNAMIC_EXCEPTION.create(e);
            }

            return SINGLE_SUCCESS;
        })).then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            if (IRCClient.get().isConnected() && IRCClient.get().isLoggedIn()) {
                if (!IRCClient.get().getConfig().isChatEnabled())
                    throw IRC_DISABLED.create();
                String message = StringArgumentType.getString(context, "message");
                IRCClient.get().sendPacket(new IRCPacketMessageSend(message));
            } else {
                throw IRC_NOT_LOGGED_IN.create();
            }
            return SINGLE_SUCCESS;
        })).then(literal("reconnect").executes(context -> {
            MessageUtils.printChatMessageWithPrefix("§aReconnecting...");
            if (IRCClient.get().isPreConnected()) IRCClient.get().disconnect();
            IRCClient.get().connect();
            return SINGLE_SUCCESS;
        }));
    }
}
