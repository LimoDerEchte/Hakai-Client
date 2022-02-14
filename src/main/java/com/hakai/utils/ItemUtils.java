package com.hakai.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;

public class ItemUtils {

    public final static SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(new LiteralText("You must be in creative mode to use this."));

    public static void giveItem(ItemStack itemStack) {
        if(itemStack == null)
            return;
        PlayerInventory inv = MinecraftClient.getInstance().player.getInventory();
        int slot = -1;
        ItemStack itemStack1 = null;
        for(int i=0; i < 9; i++) {
            itemStack1 = inv.main.get(i);
            if(itemStack1 == null || itemStack1.isEmpty()) {
                slot = i;
                break;
            }
            if(itemStack1 != null && itemStack1.getItem() == itemStack.getItem()) {
                if(!itemStack.hasNbt() && !itemStack1.hasNbt()) {
                    slot = i;
                    break;
                } else if(itemStack.hasNbt() && itemStack1.hasNbt() && itemStack.hasNbt() == itemStack1.hasNbt()) {
                    slot = i;
                    break;
                }
            }
        }

        if(slot != -1) {
            int count = itemStack1.getCount() + itemStack.getCount();
            if(count > itemStack.getMaxCount())
                count = 64;
            itemStack = itemStack.copy();
            itemStack.setCount(count);
        }

        giveItem(slot+36, itemStack);
    }

    public static void giveItem(int slot, ItemStack itemStack) {
        if(slot == -1)
            slot = MinecraftClient.getInstance().player.getInventory().selectedSlot + 36;
        MinecraftClient.getInstance().player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slot, itemStack));
    }

    public static void checkCreative() throws CommandSyntaxException {
        if (!MinecraftClient.getInstance().player.getAbilities().creativeMode)
            throw NOT_IN_CREATIVE.create();
    }

    public static void quickMoveAllShulkerItems(){
        for(int i = 0; i < 27; i ++){
            quickMoveShulkerItem(i);
        }
    }

    public static void quickMoveShulkerItem(int slot) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player.currentScreenHandler instanceof ShulkerBoxScreenHandler) {
            ShulkerBoxScreenHandler screenHandler = (ShulkerBoxScreenHandler)client.player.currentScreenHandler;
            Int2ObjectArrayMap<ItemStack> stack = new Int2ObjectArrayMap();
            stack.put(slot, screenHandler.getSlot(slot).getStack());
            client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(screenHandler.syncId, 0, slot, 0, SlotActionType.QUICK_MOVE, screenHandler.getSlot(0).getStack(), stack));
        }
    }

    public static void quickMoveAllItems(){
        for(int i = 0; i < 27; i ++){
            quickMoveItem(i);
        }
    }

    public static void quickMoveItem(int slot) {
        MinecraftClient client = MinecraftClient.getInstance();
        ScreenHandler screenHandler = client.player.currentScreenHandler;
        client.interactionManager.clickSlot(screenHandler.syncId, slot + screenHandler.slots.size() - 36, 0, SlotActionType.QUICK_MOVE, client.player);
    }
}
