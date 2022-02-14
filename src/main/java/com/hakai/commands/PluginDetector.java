package com.hakai.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import net.minecraft.command.CommandSource;

public class PluginDetector extends Command {

    public PluginDetector() {
        super("plugins", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

    }
}
