package com.hakai.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import net.minecraft.command.CommandSource;

public class Backdoor extends Command {

    public Backdoor() {
        super("backdoor", "<backdoor> [args]");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("limeploit")
                .then(literal("op").executes((context) -> {
                    return SINGLE_SUCCESS;
                })).then(literal("kick").then(argument("user", StringArgumentType.greedyString()).then(argument("reason", StringArgumentType.greedyString()).executes((context) -> {
                    return SINGLE_SUCCESS;
                })))).then(literal("console").then(argument("command", StringArgumentType.greedyString()).executes((context -> {
                    return SINGLE_SUCCESS;
                })))).then(literal("execute").then(argument("command", StringArgumentType.greedyString()).executes((context -> {
                    return SINGLE_SUCCESS;
                })))));
    }

    public void sendOnChannel(String channel, String[] data){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        for(String d : data){
            out.writeUTF(d);
        }

    }
}
