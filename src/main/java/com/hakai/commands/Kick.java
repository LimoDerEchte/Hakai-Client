package com.hakai.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.ItemUtils;
import com.hakai.utils.MessageUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;

public class Kick extends Command {
    public int CIC_EGG_LENGTH = 200;

    public Kick() {
        super("kick", "<method> [args]");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("cic-egg").executes(context -> {
            ItemStack stack = new ItemStack(Items.DRAGON_EGG, 1);
            ItemUtils.checkCreative();
            String tag = "{display:{Name:'{\"text\":\"";
            for (int i = 0; i < CIC_EGG_LENGTH; i++){
                tag += "§a§c";
            }
            stack.setNbt(StringNbtReader.parse(tag + "\"}'}}"));
            ItemUtils.giveItem(stack);
            MessageUtils.printChatMessageWithPrefix("§aDas Item wurde erstellt.");
            return SINGLE_SUCCESS;
        }));
    }
}
