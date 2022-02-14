package com.hakai.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.ItemUtils;
import com.hakai.utils.MessageUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;

public class ForceOP extends Command{
	//{title:"Hi :)",author:"Limo",pages:['{"text":"\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n__________________","clickEvent":{"action":"run_command","value":"Test"}}','{"text":""}']}

	public ForceOP() {
		super("forceop", "<method> [args]");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("book").then(argument("command", StringArgumentType.greedyString()).executes(context -> {
			ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
			String nbt = "{title:\"Hi :)\",author:\"" + MinecraftClient.getInstance().player.getName().getString();
			nbt += "\",pages:['{\"text\":\"\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n\\\\n------------------------------------------------------------------------------------------------------------------------\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"";
			nbt += StringArgumentType.getString(context, "command") + "\"}}','{\"text\":\"\"}']}";
			stack.setNbt(StringNbtReader.parse(nbt));
			ItemUtils.giveItem(stack);
			MessageUtils.printChatMessageWithPrefix("§aDas Item wurde erstellt.");
		   	return SINGLE_SUCCESS;
		})));
	}
}
