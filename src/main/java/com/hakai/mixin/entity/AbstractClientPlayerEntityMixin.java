package com.hakai.mixin.entity;

import com.hakai.utils.CapeManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {

    @Shadow @Nullable protected abstract PlayerListEntry getPlayerListEntry();

    @Inject(method = "getCapeTexture", at = @At("TAIL"))
    public void getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        if(CapeManager.fromProfile(this.getPlayerListEntry().getProfile().getId()).getTexture() != null){
            cir.setReturnValue(CapeManager.fromProfile(this.getPlayerListEntry().getProfile().getId()).getTexture());
        }
    }
}
