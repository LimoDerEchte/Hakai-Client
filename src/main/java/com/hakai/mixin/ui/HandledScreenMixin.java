package com.hakai.mixin.ui;

import com.hakai.commands.Dupe;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HandledScreen.class)
public class HandledScreenMixin<T extends ScreenHandler> {

    @Shadow @Final protected T handler;

    @Inject(method = "init", at = @At("TAIL"))
    public void onInit(CallbackInfo ci){
        if(handler instanceof ShulkerBoxScreenHandler){
            Dupe.shulkerOpen = true;
        }
    }
}
