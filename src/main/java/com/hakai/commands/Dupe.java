package com.hakai.commands;

import com.google.common.collect.Lists;
import com.hakai.main.HakaiClient;
import com.hakai.utils.ItemUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.command.Command;
import com.hakai.utils.MessageUtils;
import com.hakai.utils.toast.AdvancementMessage;
import com.hakai.utils.toast.ToastIcon;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Optional;

public class Dupe extends Command {
    public static boolean shulkerDupe = false;
    public static boolean shulkerOpen = false;
    public static boolean twofadupe = false;
    private static int twofadelay = 20;

    public Dupe() {
        super("dupe", "<method> [args]");
        HakaiClient.getInstance().registerHUDElement(() -> {
            if(shulkerDupe) return "Shulker Dupe";
            return null;
        });
        HakaiClient.getInstance().registerHUDElement(() -> {
            if(twofadupe) return "2FA Dupe";
            return null;
        });
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("shulker").executes(context -> {
            shulkerDupe = !shulkerDupe;
            if(shulkerDupe) {
                MessageUtils.printChatMessageWithPrefix("§aÖffne eine Shulkerbox um den Inhalt zu dupen");
                AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§aShulker Dupe wurde aktiviert."), ToastIcon.WARNING);
            }else
                AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§cShulker Dupe wurde deaktiviert."), ToastIcon.WARNING);

            return SINGLE_SUCCESS;
        })).then(literal("2fa-map").executes(context -> {
            twofadupe = !twofadupe;
            if(twofadupe) {
                MessageUtils.printChatMessageWithPrefix("§aÖffne einen Container und lasse deinen ersten Hotbar Slot frei um die 2FA Maps zu dupen");
                AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§a2FA Dupe wurde aktiviert."), ToastIcon.WARNING);
            }else
                AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§c2FA Dupe wurde deaktiviert."), ToastIcon.WARNING);

            return SINGLE_SUCCESS;
        })).then(literal("randombook").executes(context -> {
            List<String> pages = Lists.newArrayList();
            for(int i = 0; i < 100; i++){
                pages.add("ӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜӜ");
            }

            int i = MinecraftClient.getInstance().player.getInventory().selectedSlot;
            MinecraftClient.getInstance().interactionManager.interactItem(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND);
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new BookUpdateC2SPacket(i, pages, Optional.of("Lag-Buch")));
            return SINGLE_SUCCESS;
        }));
    }

    public static void tick(){
        if(MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().player.currentScreenHandler == null) return;
        if(MinecraftClient.getInstance().player.currentScreenHandler instanceof ShulkerBoxScreenHandler && shulkerDupe){
            if(!(MinecraftClient.getInstance().player.currentScreenHandler instanceof ShulkerBoxScreenHandler)){
                shulkerOpen = false;
                return;
            }
            HitResult hit = MinecraftClient.getInstance().crosshairTarget;
            if (hit instanceof BlockHitResult) {
                BlockHitResult blockHit = (BlockHitResult)hit;
                if (MinecraftClient.getInstance().world.getBlockState(blockHit.getBlockPos()).getBlock() instanceof ShulkerBoxBlock) {
                    MinecraftClient.getInstance().interactionManager.updateBlockBreakingProgress(blockHit.getBlockPos(), Direction.UP);
                } else {
                    MinecraftClient.getInstance().player.closeHandledScreen();
                }
            }
        }
        if(twofadupe /*&& MinecraftClient.getInstance().player.currentScreenHandler instanceof GenericContainerScreenHandler*/){
            twofadelay--;
            if(twofadelay == 11){
                ItemUtils.quickMoveItem(27);
            }else if(twofadelay <= 0){
                twofadelay = 22;
                MinecraftClient.getInstance().player.sendChatMessage("/2fa");
            }
        }
    }
}
