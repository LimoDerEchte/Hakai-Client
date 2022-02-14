package com.hakai.main;

import com.hakai.command.CommandManager;
import com.hakai.commands.*;
import com.hakai.irc.client.IRCClient;
import com.hakai.module.ModuleManager;
import com.hakai.tabui.TabUI;
import com.hakai.utils.DiscordRichPresence;
import com.hakai.utils.TPS;
import com.hakai.utils.config.YellowSnowConfig;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class HakaiClient implements ClientModInitializer {
	
	private static HakaiClient instance = null;

	public static HakaiClient getInstance() {
		return instance;
	}

	public final static String VERSION = "Alpha 1.4";

	public static TabUI tabUI;
	public static DiscordRichPresence richPresence;
	private final List<Supplier<String>> hudElements = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		instance = this;
		YellowSnowConfig config = YellowSnowConfig.get();

		TPS.registerHUD();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				IRCClient.get().disconnect();
				YellowSnowConfig.get().save();
				LogManager.getLogger().info("YellowSnow config saved!");
			} catch (IOException e) {
				LogManager.getLogger().error("YellowSnow config failed to save", e);
			}
		}, "YS-ShutdownHook"));

		registerCommands(CommandManager.get());
		registerModules(ModuleManager.get());

		tabUI = new TabUI();
		tabUI.init();

		IRCClient ircClient = IRCClient.get();
		try {
			if(!config.load()) {
				config.save();
			}
			LogManager.getLogger().info("YellowSnow config loaded!");
		} catch (IOException e) {
			LogManager.getLogger().error("YellowSnow config failed to load", e);
		}

		ircClient.connect();
		richPresence = new DiscordRichPresence("Main Menu");
		richPresence.start();
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			HakaiClient.richPresence.tick();
		}, 1, 1, TimeUnit.SECONDS);
		StructHolo.initFolder();
	}

	private void registerCommands(CommandManager commandManager) {
		commandManager.registerCommand(new ServerCommand());
		commandManager.registerCommand(new FastplaceCommand());
		commandManager.registerCommand(new Fullbright());
		commandManager.registerCommand(new RGBCommand());
		commandManager.registerCommand(new GiveCommand());
		commandManager.registerCommand(new HologramCommand());
		commandManager.registerCommand(new ImageHologramCommand());
		commandManager.registerCommand(new PvPCommand());
		commandManager.registerCommand(new IRCCommand());
		commandManager.registerCommand(new Crash());
		commandManager.registerCommand(new ForceOP());
		commandManager.registerCommand(new BlockHolo());
		commandManager.registerCommand(new Kick());
		commandManager.registerCommand(new StructHolo());
		commandManager.registerCommand(new CapeCommand());
		commandManager.registerCommand(new Dupe());
		
		commandManager.registerCommand(new HelpCommand(commandManager));
	} 

	private void registerModules(ModuleManager moduleManager) {

	}

	public void registerHUDElement(Supplier<String> consumer) {
		hudElements.add(consumer);
	}

	public List<Supplier<String>> getHudElements() {
		return hudElements;
	}

	public int getRGB() {
		return RGBCommand.getRGB();
	}
}
