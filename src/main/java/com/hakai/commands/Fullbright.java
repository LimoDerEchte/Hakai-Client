package com.hakai.commands;

import com.hakai.main.HakaiClient;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.toast.AdvancementMessage;
import com.hakai.utils.toast.ToastIcon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class Fullbright extends Command {
    public static boolean active;
    private static double gamma;

    public Fullbright() {
        super("fullbright", "Keine Argumente");
        HakaiClient.getInstance().registerHUDElement(() -> {
            if(active)
                return "Fullbright";
            return null;
        });
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            active = !active;
            if (isActive()){
                gamma = MinecraftClient.getInstance().options.gamma;
                MinecraftClient.getInstance().options.gamma = 10000;
                AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§aFullbright wurde aktiviert."), ToastIcon.WARNING);
            } else {
                MinecraftClient.getInstance().options.gamma = gamma;
                AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§cFullbright wurde deaktiviert."), ToastIcon.WARNING);
            }
            return SINGLE_SUCCESS;
        });
    }
}
