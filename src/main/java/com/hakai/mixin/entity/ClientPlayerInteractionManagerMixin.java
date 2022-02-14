package com.hakai.mixin.entity;

import com.hakai.commands.ImageHologramCommand;
import com.hakai.commands.PvPCommand;
import com.hakai.commands.StructHolo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "interactBlock", at = @At("RETURN"), cancellable = true)
    public void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
        ImageHologramCommand.usedItem(player.getStackInHand(hand), hand == Hand.OFF_HAND ? 45 : (player.getInventory().selectedSlot + 36));
        StructHolo.usedItem(player.getStackInHand(hand), hand == Hand.OFF_HAND ? 45 : (player.getInventory().selectedSlot + 36));
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void onStopUsingItem(PlayerEntity player, CallbackInfo ci) {
        if ((player.getInventory().getMainHandStack().getItem().equals(Items.BOW) || player.getInventory().getMainHandStack().getItem().equals(Items.CROSSBOW)) && PvPCommand.bowInstaKill) {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            for(int i = 0; i < 100; ++i) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 1.0E-9D, mc.player.getZ(), true));
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.0E-9D, mc.player.getZ(), false));
            }
        }
    }
}
