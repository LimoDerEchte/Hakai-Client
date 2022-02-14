package com.hakai.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.hakai.command.Command;
import com.hakai.command.CommandManager;
import com.hakai.utils.MessageUtils;
import net.minecraft.command.CommandSource;

import java.util.List;

public class HelpCommand extends Command {

	private final int pageSize = 7;

	private CommandManager commandManager;
	
	public HelpCommand(CommandManager commandManager) {
		super("help", "[page]");
		this.commandManager = commandManager;
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			printHelp(1);
			return SINGLE_SUCCESS;
		}).then(argument("page", IntegerArgumentType.integer(1, getPageAmount() + 1)).executes(context -> {
			printHelp(IntegerArgumentType.getInteger(context, "page"));
			return SINGLE_SUCCESS;
		}));
	}

	public int getPageAmount(){
		float pagesFloat = (commandManager.getCommands().size() - 1) / pageSize;
		int pages = (int) pagesFloat;
		if(pagesFloat > pages) pages++;
		return pages;
	}

	private void printHelp(int page) throws CommandSyntaxException {
		List<Command> list = commandManager.getCommands();

		int j = getPageAmount();

		if(page < 1 || page > j+1)
			return;

		page--;

		int l = Math.min((page + 1) * 7, list.size());

		MessageUtils.printChatMessageWithPrefix(String.format("§6Help Page §7%d/§7%d", (page + 1), (j + 1)));
		for (int i1 = page * 7; i1 < l; ++i1) {
			MessageUtils.printChatMessage("        §7" + CommandManager.COMMAND_PREFIX + list.get(i1).getName() + " §8- §7" + list.get(i1).getDescription());
		}
		MessageUtils.printChatMessage(" ");
	}

}
