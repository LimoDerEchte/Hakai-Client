package com.hakai.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.command.arguments.HexColorArgumentType;
import com.hakai.main.HakaiClient;
import com.hakai.utils.MessageUtils;
import com.hakai.utils.config.SubConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;

import java.awt.*;

public class RGBCommand extends Command implements SubConfig {

	private static int red = 255;
	private static int green = 0;
	private static int blue = 0;

	private static int rgbFade = -1;
	private static int rgbFixed = Color.GREEN.getRGB();
	private static int speed = 10;
	private static byte fadePhase = 0;
	private static int fadePhaseCounter = 0;
	private static boolean fadeToggle = false;

	static {
		updateRGBFade();
	}

	public static void tick() {
		if(!fadeToggle)
			return;
		fadePhaseCounter += speed;
		switch(fadePhase) {
			case 0: {
				red = red >= speed ? red - speed : speed - red;
				green = green <= (255 - speed) ? green + speed : 255 - (green + speed - 255);
				if(fadePhaseCounter >= 255)
					fadePhase = 1;
				break;
			}
			case 1: {
				green = green >= speed ? green - speed : speed - green;
				blue = blue <= (255 - speed) ? blue + speed : 255 - (blue + speed - 255);
				if(fadePhaseCounter >= 255)
					fadePhase = 2;
				break;
			}
			case 2: {
				blue = blue >= speed ? blue - speed : speed - blue;
				red = red <= (255 - speed) ? red + speed : 255 - (red + speed - 255);
				if(fadePhaseCounter >= 255)
					fadePhase = 0;
				break;
			}
		}
		if(fadePhaseCounter >= 255)
			fadePhaseCounter = 0;

		updateRGBFade();
	}

	private static void updateRGBFade() {
		rgbFade = 255;
		rgbFade = (rgbFade << 8) + red;
		rgbFade = (rgbFade << 8) + green;
		rgbFade = (rgbFade << 8) + blue;
	}

	public static int getRGB() {
		return fadeToggle ? rgbFade : rgbFixed;
	}

	public RGBCommand() {
		super("rgb", "toggle|speed <speed>|color <color>");
		HakaiClient.getInstance().registerHUDElement(() -> {
			if(fadeToggle)
				return "RGB: Fade\n\u27A5 Speed: §7" + speed;
			else
				return "RGB: Fixed";
		});
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("toggle").executes(context -> {
			fadeToggle = !fadeToggle;
			MessageUtils.printChatMessageWithPrefix("§aRainbow " + (fadeToggle ? "§aAktiviert" : "§cDeaktiviert"));
			return SINGLE_SUCCESS;
		})).then(literal("speed").then(argument("speed", IntegerArgumentType.integer(1, 20)).executes(context -> {
			speed = IntegerArgumentType.getInteger(context, "speed");
			MessageUtils.printChatMessageWithPrefix("§aDie RGB-Geschwindigkeit ist nun " + speed + ".");
			return SINGLE_SUCCESS;
		}))).then(literal("color").then(argument("hexcolor", HexColorArgumentType.hexColorArgument()).executes(context -> {
			Color color = HexColorArgumentType.getColor(context, "hexcolor");
			if(color == null) {
				MessageUtils.printChatMessageWithPrefix("§cDie Farbe muss in Hex angegeben werden§r\n  §7-> §a#§fFFFFFF §7<--");
			} else {
				rgbFixed = color.getRGB();
				MessageUtils.printChatMessageWithPrefix(MessageUtils.merge(MessageUtils.toLiteralText("§aDie Farbe ist nun §a\u00BB "), new LiteralText("\u2588").styled(style -> style.withColor(TextColor.fromRgb(rgbFixed)))));
			}
			return SINGLE_SUCCESS;
		})));
	}

	@Override
	public boolean load(JsonObject obj) {
		boolean complete = true;

		if (obj.has("fadeEnabled"))
			fadeToggle = obj.get("fadeEnabled").getAsBoolean();
		else
			complete = false;

		if (obj.has("speed"))
			speed = obj.get("speed").getAsInt();
		else
			complete = false;

		if (obj.has("fixedRGB"))
			rgbFixed = obj.get("fixedRGB").getAsInt();
		else
			complete = false;

		return complete;
	}

	@Override
	public void save(JsonObject obj) {
		obj.addProperty("fadeEnabled", fadeToggle);
		obj.addProperty("speed", speed);
		obj.addProperty("fixedRGB", rgbFixed);
	}

}
