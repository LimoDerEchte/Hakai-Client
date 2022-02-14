package com.hakai.mixin.entity;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraRenderMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;canRenderCapeTexture()Z"))
    private boolean toggleCustomElytra(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return abstractClientPlayerEntity.getElytraTexture() != null;
    }

}
