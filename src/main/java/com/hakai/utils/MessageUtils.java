package com.hakai.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

	public static final Pattern URL_PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
	public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");

	public static void actionBar(String actionbar) {
		MinecraftClient.getInstance().player.sendMessage(Text.of(actionbar), true);
	}

	public static Text[] merge(Text[] array, Text... array2) {
		Text[] textArray = new Text[array.length + array2.length];
		System.arraycopy(array, 0, textArray, 0, array.length);
		System.arraycopy(array2, 0, textArray, array.length, array2.length);
		return textArray;
	}

	public static void printChatMessage(Text... text) {
		Text single = toSingleText(text);
		if(MinecraftClient.getInstance().player == null)
			LogManager.getLogger().info((String)"[CHAT] {}", (Object)single.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
		else
			MinecraftClient.getInstance().player.sendMessage(single, false);
	}

	public static void printChatMessage(Formatting formatting, String message) {
		printChatMessage(toLiteralText(message, formatting));
	}

	public static void printChatMessage(String message) {
		printChatMessage(Formatting.GRAY, message);
	}

	public static void printChatMessageWithPrefix(Text... text) {
		printChatMessage(merge(toLiteralText("§8[§cH§8] §r§7"), text));
	}

	public static void printChatMessageWithPrefix(Formatting formatting, String message) {
		printChatMessageWithPrefix(toLiteralText(message, formatting));
	}

	public static void printChatMessageWithPrefix(String message) {
		printChatMessageWithPrefix(toLiteralText(message, Formatting.GRAY));
	}

	public static Text toSingleText(Text[] text) {
		Text rootText = text[0];
		if(text.length != 1) {
			MutableText rootText2;
			if(rootText instanceof MutableText)
				rootText2 = (MutableText) rootText;
			else
				rootText2 = new LiteralText("").append(rootText);
			for(int i=1; i < text.length; i++)
				rootText2.append(text[i]);
			rootText = rootText2;
		}
		return rootText;
	}

	public static LiteralText[] toLiteralText(String message) {
		return toLiteralText(message, null);
	}

	public static LiteralText[] toLiteralText(String message, Formatting defaultFormatting) {
		List<LiteralText> components = new ArrayList<LiteralText>();
		StringBuilder builder = new StringBuilder();
		Formatting formatting;
		TextColor textColor;
		Style style = Style.EMPTY;
		if(defaultFormatting != null)
			style = style.withColor(defaultFormatting);
		Matcher matcher = URL_PATTERN.matcher(message);

		char c;
		for(int i=0; i < message.length(); i++) {
			c = message.charAt(i);
			if(c == '§') {
				if(++i >= message.length())
					break;

				c = Character.toLowerCase(message.charAt(i));

				if("0123456789abcdefklmnorx".indexOf(c) > -1) {
					formatting = null;
					textColor = null;
					if (c == 'x' && i + 7 < message.length()) {

						StringBuilder hex = new StringBuilder();
						for (int j = 0; j < 6; j++)
						{
							hex.append(message.charAt(i + 2 + j));
						}

						try {
							int rgb = new Color(Integer.valueOf( hex.substring( 1, 3 ), 16 ),
									Integer.valueOf( hex.substring( 3, 5 ), 16 ),
									Integer.valueOf( hex.substring( 5, 7 ), 16 ) ).getRGB();
							textColor = TextColor.fromRgb(rgb);
						} catch (NumberFormatException e) {}

						i += 12;
					} else {
						formatting = Formatting.byCode(c);
						if(formatting.isColor()) {
							textColor = TextColor.fromFormatting(formatting);
							formatting = null;
						}
					}

					boolean push = false;
					if(formatting != null) {
						if(formatting == Formatting.RESET) {
							formatting = null;
							if(defaultFormatting == null) {
								push = true;
							} else {
								textColor = TextColor.fromFormatting(defaultFormatting);
							}
						} else {
							style = style.withFormatting(formatting);
						}
					}
					if(textColor != null)
						push = true;
					if(push) {
						if(builder.length() > 0) {
							LiteralText text = new LiteralText(builder.toString());
							text.setStyle(style);
							components.add(text);
							builder = new StringBuilder();
						}
						style = Style.EMPTY.withColor(textColor);
					}
				} else {
					builder.append('?');
					builder.append(c);
				}
			} else {
				int pos = message.indexOf(' ', i);
				if(pos == -1)
					pos = message.length();
				if(matcher.region(i, pos).find()) {

					if(builder.length() > 0) {
						LiteralText text = new LiteralText(builder.toString());
						text.setStyle(style);
						components.add(text);
					}

					String urlString = message.substring(i, pos);
					LiteralText text = new LiteralText(urlString);
					text.setStyle(style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlString.startsWith("http") ? urlString : ("http://" + urlString))));
					i = pos - 1;
				} else {
					builder.append(c);
				}
			}
		}

		if(builder.length() > 0) {
			LiteralText text = new LiteralText(builder.toString());
			text.setStyle(style);
			components.add(text);
		}

		return components.toArray(new LiteralText[components.size()]);
	}

	public static String replaceColorCodes(char c, String s) {
		char[] b = s.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = '§';
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

	public static String stripColor(String input) {
		if(input == null)
			return null;
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

}
