package com.hakai.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.ItemUtils;
import com.hakai.utils.MessageUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class BlockHolo extends Command{

	public BlockHolo() {
		super("blockholo", "<block> <text>");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("block", BlockStateArgumentType.blockState())
			   .then(argument("text", StringArgumentType.greedyString()).executes(context -> {

			ItemUtils.checkCreative();

			Item item = context.getArgument("block", BlockStateArgument.class).getBlockState().getBlock().asItem();
			String text = StringArgumentType.getString(context, "text");
			String jsonText = Text.Serializer.toJson(MessageUtils.toSingleText(MessageUtils.toLiteralText(text)));

			String blockId = Registry.ITEM.getId(item).toString();

			ItemStack itemstack = new ItemStack(Items.ARMOR_STAND, 1);

			double x = MinecraftClient.getInstance().player.getX();
			double y = MinecraftClient.getInstance().player.getY();
			double z = MinecraftClient.getInstance().player.getZ();

			itemstack.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"Hologram\",\"color\":\"yellow\",\"italic\":false}'},EntityTag:{ArmorItems:[{},{},{},{id:\"" + blockId + "\",Count:1b}],Pos:[" + x + "," + y + "," + z + "],CustomNameVisible:1b,NoGravity:1b,Small:1b,Invisible:1b,CustomName:'" + jsonText.replace("&", "§") + "'}}"));

			MessageUtils.printChatMessageWithPrefix("§aHologram-Item wurde erstellt.");

			ItemUtils.giveItem(itemstack);

			return SINGLE_SUCCESS;
		})));
	}

}
