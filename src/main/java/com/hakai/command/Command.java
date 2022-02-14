package com.hakai.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandSource;

public abstract class Command {

	protected static final int SINGLE_SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;

	protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}

	protected static <T> LiteralArgumentBuilder<CommandSource> literal(final String name) {
		return LiteralArgumentBuilder.literal(name);
	}

	private final String command, description;
	
	public Command(String command, String description) {
		this.command = command;
		this.description = description;
	}
	
	public Command(String command) {
		this.command = command;
		this.description = null;
	}

	public final String getName() {
		return command;
	}

	public final String getDescription() {
		return description;
	}

	public abstract void build(LiteralArgumentBuilder<CommandSource> builder);

}
