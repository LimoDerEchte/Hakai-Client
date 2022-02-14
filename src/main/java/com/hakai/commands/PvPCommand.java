package com.hakai.commands;

import com.hakai.utils.ItemUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.toast.AdvancementMessage;
import com.hakai.utils.toast.ToastIcon;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;

public class PvPCommand extends Command {
    public static boolean bowInstaKill = false;

    public PvPCommand() {
        super("pvp", "<bowinstakill>");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("bowinstakill").executes((context) -> {
            bowInstaKill = !bowInstaKill;
            if(bowInstaKill)
                AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§aBow-Instakill wurde aktiviert."), ToastIcon.WARNING);
            else
                AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§cBow-Instakill wurde deaktiviert."), ToastIcon.WARNING);
            return SINGLE_SUCCESS;
        })).then(literal("instakill-potion").executes((context) -> {
            ItemUtils.checkCreative();
            ItemStack item = new ItemStack(Items.SPLASH_POTION);
            item.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"InstaKill\",\"color\":\"red\",\"bold\":true}',Lore:['{\"text\":\"Selbst Leute im Krea :O\",\"color\":\"dark_red\",\"italic\":false}']},CustomPotionEffects:[{Id:6b,Amplifier:125b,Duration:100}],Potion:\"minecraft:harming\",CustomPotionColor:16711680}"));
            ItemUtils.giveItem(item);
            return SINGLE_SUCCESS;
        }));
    }
}
