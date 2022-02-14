package com.hakai.mixin.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.hakai.utils.CapeManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Map<Type, Identifier> textures;

    @Inject(method = "loadTextures", at = @At("TAIL"))
    private void loadTextures(CallbackInfo ci) {
        Identifier cape = CapeManager.fromProfile(profile.getId()).getTexture();
        if (cape != null) {
            this.textures.put(Type.CAPE, cape);
        }
    }

}
