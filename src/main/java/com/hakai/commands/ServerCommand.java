package com.hakai.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.MessageUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerCommand extends Command{

	public ServerCommand() {
		super("server", "Keine Argumente");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			if(MinecraftClient.getInstance().isIntegratedServerRunning()) {
				IntegratedServer server = MinecraftClient.getInstance().getServer();

				MessageUtils.printChatMessageWithPrefix("Singleplayer");
				if(server != null)
					MessageUtils.printChatMessageWithPrefix("§aVersion: §7" + server.getVersion());

				return SINGLE_SUCCESS;
			}

			ServerInfo server = MinecraftClient.getInstance().getCurrentServerEntry();

			if (server == null) {
				MessageUtils.printChatMessageWithPrefix("§cCouldn't obtain any server information.");
				return SINGLE_SUCCESS;
			}

			String ipv4 = "";
			try {
				ipv4 = InetAddress.getByName(server.address).getHostAddress();
			} catch (UnknownHostException ignored) {}

			MutableText ipText;

			if (ipv4.isEmpty()) {
				ipText = new LiteralText(server.address).formatted(Formatting.GRAY);
				ipText.setStyle(ipText.getStyle()
						.withClickEvent(new ClickEvent(
								ClickEvent.Action.COPY_TO_CLIPBOARD,
								server.address
						))
						.withHoverEvent(new HoverEvent(
								HoverEvent.Action.SHOW_TEXT,
								new LiteralText("Copy to clipboard")
						))
				);
			}
			else {
				ipText = new LiteralText(Formatting.GRAY + server.address);
				ipText.setStyle(ipText.getStyle()
						.withClickEvent(new ClickEvent(
								ClickEvent.Action.COPY_TO_CLIPBOARD,
								server.address
						))
						.withHoverEvent(new HoverEvent(
								HoverEvent.Action.SHOW_TEXT,
								new LiteralText("Copy to clipboard")
						))
				);
				MutableText ipv4Text = new LiteralText(String.format(" (%s)", ipv4)).formatted(Formatting.GRAY);
				ipv4Text.setStyle(ipText.getStyle()
						.withClickEvent(new ClickEvent(
								ClickEvent.Action.COPY_TO_CLIPBOARD,
								ipv4
						))
						.withHoverEvent(new HoverEvent(
								HoverEvent.Action.SHOW_TEXT,
								new LiteralText("Copy to clipboard")
						))
				);
				ipText.append(ipv4Text);
			}
			MessageUtils.printChatMessageWithPrefix(
					new LiteralText("IP: ").formatted(Formatting.GREEN)
							.append(ipText)
			);

			MessageUtils.printChatMessageWithPrefix("§aPort: §7" + ServerAddress.parse(server.address).getPort());

			MessageUtils.printChatMessageWithPrefix("§aType: §7" + MinecraftClient.getInstance().player.getServerBrand() != null ? MinecraftClient.getInstance().player.getServerBrand() : "unknown");

			//MessageUtils.printChatMessageWithPrefix("§aMotd: §7" + server.label != null ? server.label.getString() : "unknown");

			MessageUtils.printChatMessageWithPrefix("§aVersion: §7" + server.version.getString());

			MessageUtils.printChatMessageWithPrefix("§aProtocol version: §7" + server.protocolVersion);

			MessageUtils.printChatMessageWithPrefix("§aDifficulty: §7" + MinecraftClient.getInstance().world.getDifficulty().getTranslatableName().getString());

			ClientCommandSource cmdSource = MinecraftClient.getInstance().getNetworkHandler().getCommandSource();
			int permission_level = 5;
			while (permission_level > 0) {
				if (cmdSource.hasPermissionLevel(permission_level)) break;
				permission_level--;
			}
			MessageUtils.printChatMessageWithPrefix("§aPermission level: §7" + permission_level);

			return SINGLE_SUCCESS;
		});
	}
}
