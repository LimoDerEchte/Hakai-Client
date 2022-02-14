package com.hakai.mixin.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.hakai.command.CommandManager;
import com.hakai.utils.MessageUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow
    public abstract void sendChatMessage(String string);

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo info) {
        if(message.startsWith(String.valueOf(CommandManager.COMMAND_PREFIX))) {
            try {
                CommandManager.get().execute(message.substring(1));
            } catch (CommandSyntaxException e) {
                MessageUtils.printChatMessageWithPrefix(Formatting.GRAY, e.getMessage());
            }
            info.cancel();
        }
    }

}
