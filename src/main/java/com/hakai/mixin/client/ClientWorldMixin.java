package com.hakai.mixin.client;

import com.hakai.commands.AntiLag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.EntityList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = ClientWorld.class)
public class ClientWorldMixin {
/*
    @Inject(method = "addEntityPrivate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;removeEntity(I)V", shift = At.Shift.AFTER), cancellable = true)
    private void onAddEntity(int id, Entity entity, CallbackInfo info) {
    }

    @Inject(method = "removeEntity", at = @At("HEAD"), cancellable = true)
    private void onRemoveEntity(int id, CallbackInfo info) {
    }
*/

    @Shadow @Final private EntityList entityList;

    @Inject(method = "getEntities", at = @At("TAIL"))
    public void getEntities(CallbackInfoReturnable<Iterable<Entity>> cir) {
        if(!AntiLag.isActive()) return;
        List<Entity> entities = new ArrayList<>();
        entityList.forEach(entity -> {
            if(!(entity instanceof PlayerEntity))
                entities.add(entity);
        });
        cir.setReturnValue(new Iterable<Entity>() {
            @NotNull
            @Override
            public Iterator<Entity> iterator() {
                return entities.iterator();
            }
        });
    }
}
