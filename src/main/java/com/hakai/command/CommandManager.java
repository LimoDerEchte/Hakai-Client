package com.hakai.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.hakai.utils.config.SubConfig;
import com.hakai.utils.config.YellowSnowConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements SubConfig {

	public static final char COMMAND_PREFIX = '#';
	private static CommandManager instance;

	public static CommandManager get() {
		if(instance == null)
			instance = new CommandManager();
		return instance;
	}

	private CommandManager() {
		YellowSnowConfig.get().registerSubConfig("commandSettings", this);
	}

	private final ClientCommandSource commandSource = new ClientCommandSource(null, MinecraftClient.getInstance());
	private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
	private final List<Command> commands = new ArrayList<>();
	
	public void registerCommand(Command command) {
		//executor.setEventManager(PWMod.getInstance().getEventManager());
		LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(command.getName());
		command.build(builder);
		dispatcher.register(builder);
		commands.add(command);
	}

	public void execute(String command) throws CommandSyntaxException {//new ClientCommandSource(null, MinecraftClient.getInstance())
		dispatcher.execute(command, commandSource);
	}

	public ClientCommandSource getCommandSource() {
		return commandSource;
	}

	public CommandDispatcher<CommandSource> getDispatcher() {
		return dispatcher;
	}

	public List<Command> getCommands() {
		return commands;
	}

	@Override
	public boolean load(JsonObject element) {
		boolean complete = true;
		for(Command command : commands) {
			if(command instanceof SubConfig) {
				if (element.has(command.getName()))
					((SubConfig) command).load(element.getAsJsonObject(command.getName()));
				else
					complete = false;
			}
		}
		return complete;
	}

	@Override
	public void save(JsonObject element) {
		for(Command command : commands) {
			if(command instanceof SubConfig) {
				JsonObject uElement = new JsonObject();
				((SubConfig) command).save(uElement);
				element.add(command.getName(), uElement);
			}
		}
	}
}
