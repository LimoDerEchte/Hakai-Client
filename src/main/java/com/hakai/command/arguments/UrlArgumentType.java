package com.hakai.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.LiteralText;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlArgumentType implements ArgumentType<URL> {

    private final static SimpleCommandExceptionType MALFORMED_URL = new SimpleCommandExceptionType(new LiteralText("MalformedURLException"));

    public static UrlArgumentType urlArgument() {
        return new UrlArgumentType();
    }

    @Override
    public URL parse(StringReader reader) throws CommandSyntaxException {
        try {
            String line = "";
            {
                while(reader.canRead() && (reader.peek()) != ' ') {
                    line += reader.read();
                }
            }
            //System.out.println("\"" + line + "\"");
            return new URL(line);
        } catch (MalformedURLException e) {
            throw MALFORMED_URL.createWithContext(reader);
        }
    }

    public static URL getURL(final CommandContext<?> context, String name) {
        return context.getArgument(name, URL.class);
    }

}
