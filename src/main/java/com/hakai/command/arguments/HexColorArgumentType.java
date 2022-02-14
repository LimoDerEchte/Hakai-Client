package com.hakai.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.LiteralText;

import java.awt.*;

public class HexColorArgumentType implements ArgumentType<Color> {

    private final static SimpleCommandExceptionType MALFORMED_HEX = new SimpleCommandExceptionType(new LiteralText("Use #<hexCode>"));

    public static HexColorArgumentType hexColorArgument() {
        return new HexColorArgumentType();
    }

    public static Color getColor(final CommandContext<?> context, String name) {
        return context.getArgument(name, Color.class);
    }

    @Override
    public Color parse(StringReader reader) throws CommandSyntaxException {
        if(!reader.canRead(7))
            throw MALFORMED_HEX.createWithContext(reader);
        String hex = "";
        for(int i=0; i<7; i++)
            hex += reader.read();
        if(hex.length() != 7 || !hex.startsWith("#"))
            throw MALFORMED_HEX.createWithContext(reader);
        try {
            return new Color(Integer.valueOf( hex.substring( 1, 3 ), 16 ),
                    Integer.valueOf( hex.substring( 3, 5 ), 16 ),
                    Integer.valueOf( hex.substring( 5, 7 ), 16 ) );
        } catch(NumberFormatException e) {
            throw MALFORMED_HEX.createWithContext(reader);
        }
    }

}
