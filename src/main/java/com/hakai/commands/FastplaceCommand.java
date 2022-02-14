package com.hakai.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.main.HakaiClient;
import com.hakai.utils.toast.AdvancementMessage;
import com.hakai.utils.toast.ToastIcon;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class FastplaceCommand extends Command{

	public static boolean active;

	public FastplaceCommand() {
		super("fastplace", "Keine Argumente");
		HakaiClient.getInstance().registerHUDElement(() -> {
			if(active)
				return "Fastplace";
			return null;
		});
	}
	
	public static boolean isActive() {
		return active;
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			active = !active;
			if(isActive())
				AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§aFastplace wurde aktiviert."), ToastIcon.WARNING);
			else
				AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§cFastplace wurde deaktiviert."), ToastIcon.WARNING);
			return SINGLE_SUCCESS;
		});
	}
}
