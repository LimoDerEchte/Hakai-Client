package com.hakai.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import net.minecraft.command.CommandSource;

public class BigHolo extends Command {
    public BigHolo() {
        super("bigholo", "Spawne Text aus vielen Texthologrammen");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("size", IntegerArgumentType.integer(1, 5)).then(argument("text", StringArgumentType.greedyString()).executes(context -> {


            return  SINGLE_SUCCESS;
        })));
    }
}
