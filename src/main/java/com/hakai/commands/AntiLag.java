package com.hakai.commands;

import com.hakai.main.HakaiClient;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.MessageUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;

public class AntiLag extends Command {

	private static boolean active = false;
	
	public AntiLag() {
		super("antilag");
		HakaiClient.getInstance().registerHUDElement(() -> {
			if(active) {
				return "AntiLag";
			}
			return null;
		});
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			ClientWorld world = MinecraftClient.getInstance().world;
			active = !active;
			if(active) {
				MessageUtils.printChatMessageWithPrefix("§aAntiLag wurde aktiviert.");
			} else {
				MessageUtils.printChatMessageWithPrefix("§cAntiLag wurde deaktiviert.");
			}
			return SINGLE_SUCCESS;
		});
	}

	public static boolean isActive() {
		return active;
	}
}
