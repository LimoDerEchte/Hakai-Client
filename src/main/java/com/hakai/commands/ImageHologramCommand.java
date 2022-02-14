package com.hakai.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.hakai.command.Command;
import com.hakai.command.arguments.UrlArgumentType;
import com.hakai.utils.ItemUtils;
import com.hakai.utils.MessageUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.LiteralText;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Objects;

public class  ImageHologramCommand extends Command {

	private final static SimpleCommandExceptionType HOLOGRAM_STILL_ACTIVE = new SimpleCommandExceptionType(new LiteralText("Structure or Image Hologram still active."));
	private final static SimpleCommandExceptionType DL_EXCEPTION_IMAGE = new SimpleCommandExceptionType(new LiteralText("Error by downloading Image"));

	private static ItemStack lastHologramItem = null;
	
	public static ItemStack[] items = null;
	private static int index = -1;
	
	public ImageHologramCommand() {
		super("imgholo", "cancel|<url> <width>");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("cancel").executes(context -> {
			index = 0;
			items = null;
			lastHologramItem = null;
			MessageUtils.printChatMessageWithPrefix("§aImage Hologram §cabgebrochen!");
			return SINGLE_SUCCESS;
		})).then(argument("imageUrl", UrlArgumentType.urlArgument()).then(argument("size", IntegerArgumentType.integer(1,1020)).executes(context -> {
			ItemUtils.checkCreative();

			if(items != null || StructHolo.items != null)
				throw HOLOGRAM_STILL_ACTIVE.create();

			URL imageUrl = UrlArgumentType.getURL(context, "imageUrl");

			int width;
			int height;

			BufferedImage image;
		   	try {
				System.out.println("\"" + imageUrl.toString() + "\"");
		   		image = ImageIO.read(imageUrl);
		   		if(image == null)
		   			throw new NullPointerException("Image is null");

				width = IntegerArgumentType.getInteger(context, "size");
				height = image.getHeight() * width / image.getWidth();

				image = rescaleImage(image, width, height);
		   	} catch (Exception e) {
		   		e.printStackTrace();
		   		throw DL_EXCEPTION_IMAGE.create();
		   	}

			index = 0;
			items = new ItemStack[height];

			double armorStandDistance = 0.25;

			double posX = MinecraftClient.getInstance().player.getX();
			double posY = MinecraftClient.getInstance().player.getY() + (height * armorStandDistance);
			double posZ = MinecraftClient.getInstance().player.getZ();

			for(int y=0; y < height; y++) {
				StringBuilder jsonDisplayName = new StringBuilder("[");
				for(int x=0; x < width; x++) {
					Color color = new Color(image.getRGB(x, y));
					String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
					jsonDisplayName.append("{\"text\":\"\\\\u2588\",\"color\":\"").append(hex).append("\"},");
				}

				items[y] = createHologram(jsonDisplayName.substring(0, jsonDisplayName.length() - 1) + "]", posX, posY - (y * armorStandDistance), posZ);
			}

			nextItem(-1);
			return SINGLE_SUCCESS;
		})));
	}

	private ItemStack createHologram(String jsonDisplayName, double x, double y, double z) {
		ItemStack itemstack = new ItemStack(Items.ARMOR_STAND, 1);
        try {
			itemstack.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"Hologram\",\"color\":\"yellow\",\"italic\":false}'},EntityTag:{Pos:[" + x + "," + y + "," + z + "],CustomNameVisible:1b,NoGravity:1b,NoBasePlate:1b,Invisible:1b,CustomName:'" + jsonDisplayName + "'}}"));
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
        return itemstack;
	}

	public static void usedItem(ItemStack itemstack, int slot) {
		if(itemstack == null || lastHologramItem == null || !itemstack.hasNbt() || !Objects.equals(itemstack.getNbt(), lastHologramItem.getNbt()))
			return;
		nextItem(slot);
	}

	private static void nextItem(int slot) {
		try {
			ItemUtils.checkCreative();
		} catch (CommandSyntaxException e) { return; }
		if(index >= items.length) {
			index = 0;
			items = null;
			lastHologramItem = null;
			ItemUtils.giveItem(slot, ItemStack.EMPTY);
			MessageUtils.printChatMessageWithPrefix("§aAlle Armorstands Platziert!");
			return;
		}
		MessageUtils.actionBar("§aNoch §7" + (items.length - index) + " §aArmorstands übrig.");
		lastHologramItem = items[index];
		ItemUtils.giveItem(slot, lastHologramItem);
		index++;
	}
	
	private BufferedImage rescaleImage(Image image, int width, int height) {
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = bimg.getGraphics();
		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
		return bimg;
	}

}
