package com.hakai.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.hakai.command.Command;
import com.hakai.irc.client.IRCClient;
import com.hakai.irc.protocol.packets.capes.IRCPacketGetAvailableCapes;
import com.hakai.utils.CapeManager;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

public class CapeCommand extends Command {
    private final static SimpleCommandExceptionType IRC_NOT_LOGGED_IN = new SimpleCommandExceptionType(new LiteralText("IRCChat must be logged in"));
    public static String changeCape = "";

    public CapeCommand(){
        super("cape", "list|forcereload|<cape>");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("list").executes((context -> {
            if(IRCClient.get().isConnected() && IRCClient.get().isLoggedIn()){
                IRCClient.get().sendPacket(new IRCPacketGetAvailableCapes());
            }else {
                throw IRC_NOT_LOGGED_IN.create();
            }
            return SINGLE_SUCCESS;
        }))).then(argument("cape", StringArgumentType.greedyString()).executes((context -> {
            if(IRCClient.get().isConnected() && IRCClient.get().isLoggedIn()){
                changeCape = StringArgumentType.getString(context, "cape");
                IRCClient.get().sendPacket(new IRCPacketGetAvailableCapes());
            }else {
                throw IRC_NOT_LOGGED_IN.create();
            }
            return SINGLE_SUCCESS;
        }))).then(literal("forcereload").executes((context -> {
            CapeManager.updateAllCapes();
            return SINGLE_SUCCESS;
        })));
    }
}
