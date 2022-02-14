package com.hakai.mixin.client;

import com.hakai.commands.Crash;
import com.hakai.commands.Dupe;
import com.hakai.commands.FastplaceCommand;
import com.hakai.commands.RGBCommand;
import com.hakai.irc.client.IRCClient;
import com.hakai.main.HakaiClient;
import com.hakai.utils.CapeManager;
import com.hakai.utils.TPS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow private int itemUseCooldown;

    @Shadow public abstract Session getSession();

    private Session lastSession = null;

    @Redirect(method = "doItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I", opcode = Opcodes.PUTFIELD))
    public void setItemUseCooldown(MinecraftClient client, int i) {
        if(!FastplaceCommand.isActive())
            this.itemUseCooldown = i;
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo info) {
        RGBCommand.tick();
        Crash.tick();
        CapeManager.tick();
        Dupe.tick();
        TPS.tick();
    }

    @Inject(at = @At("TAIL"), method = "setScreen")
    private void onSetScreen(CallbackInfo info) {
        IRCClient.get().updateMinecraftSession(this.getSession());
    }

}
