package com.hakai.mixin.client;

import com.hakai.commands.Dupe;
import com.hakai.utils.ItemUtils;
import com.hakai.utils.TPS;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "disconnect", at = @At("TAIL"))
    private void onDisconnect(Text reason, CallbackInfo info) {

    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void onHandlePacket(Packet<?> packet, PacketListener listener, CallbackInfo info) {
        if(packet instanceof WorldTimeUpdateS2CPacket) {
            TPS.serverWorldTimePacket();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    private void onSendingPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo info) {

    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("TAIL"), cancellable = true)
    private void onSentPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo info) {
        if (packet instanceof PlayerActionC2SPacket && ((PlayerActionC2SPacket)packet).getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
            if (Dupe.shulkerOpen && Dupe.shulkerDupe) {
                if (packet instanceof PlayerActionC2SPacket && ((PlayerActionC2SPacket)packet).getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
                    ItemUtils.quickMoveAllShulkerItems();
                }
            }
        }
    }

}
