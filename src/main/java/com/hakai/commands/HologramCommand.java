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
import net.minecraft.text.Text;

public class HologramCommand extends Command{

	public HologramCommand() {
		super("hologram", "<text>");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("text", StringArgumentType.greedyString()).executes(context -> {
			String text = StringArgumentType.getString(context, "text");
			String jsonText = Text.Serializer.toJson(MessageUtils.toSingleText(MessageUtils.toLiteralText(text)));

			ItemUtils.checkCreative();

			ItemStack itemstack = new ItemStack(Items.ARMOR_STAND, 1);

			double x = MinecraftClient.getInstance().player.getX();
			double y = MinecraftClient.getInstance().player.getY();
			double z = MinecraftClient.getInstance().player.getZ();

			itemstack.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"Hologram\",\"color\":\"yellow\",\"italic\":false}'},EntityTag:{Pos:[" + x + "," + y + "," + z + "],CustomNameVisible:1b,NoGravity:1b,Small:1b,Invisible:1b,CustomName:'" + jsonText.replace('&', '§') + "'}}"));

			ItemUtils.giveItem(itemstack);
			MessageUtils.printChatMessageWithPrefix("§aHologram-Item wurde erstellt.");
			return SINGLE_SUCCESS;
		}));
	}

/*	@Override
	public boolean execute(String[] args) {
		if(args.length == 0)
			return false;

    	if(!GiveCommand.checkCreative())
    		return true;
    	
        ItemStack itemstack = new ItemStack(Items.ARMOR_STAND, 1);
        
        double x = MinecraftClient.getInstance().player.getX();
        double y = MinecraftClient.getInstance().player.getY();
        double z = MinecraftClient.getInstance().player.getZ();

        String s = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        
        try {
			itemstack.setTag(StringNbtReader.parse("{display:{Name:'{\"text\":\"Hologram\",\"color\":\"yellow\",\"italic\":false}'},EntityTag:{Pos:[" + x + "," + y + "," + z + "],CustomNameVisible:1b,NoGravity:1b,Small:1b,Invisible:1b,CustomName:'{\"text\":\"" + s.replace("&", "§").replace("\"", "\\\\\"") + "\"}'}}"));
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}

		GiveCommand.giveItem(itemstack, 36);
        MessageUtils.printChatMessageWithPrefix("§aHologram-Item wurde erstellt.");
        
		return true;
	}*/

}
