package com.hakai.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.ItemUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;

public class GiveCommand extends Command {

    public GiveCommand() {
        super("give", "<item[{nbt}]> [count]");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("item", ItemStackArgumentType.itemStack()).executes(context -> {
            ItemUtils.checkCreative();

            ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false);
            ItemUtils.giveItem(item);

            return SINGLE_SUCCESS;
        }).then(argument("amount", IntegerArgumentType.integer(1, 64)).executes(context -> {
            ItemUtils.checkCreative();

            ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(IntegerArgumentType.getInteger(context, "amount"), false);
            ItemUtils.giveItem(item);

            return SINGLE_SUCCESS;
        })));
    }

}
