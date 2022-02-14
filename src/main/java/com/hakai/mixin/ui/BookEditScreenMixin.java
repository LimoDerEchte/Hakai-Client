package com.hakai.mixin.ui;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BookEditScreen.class)
public class BookEditScreenMixin {
/*
    @Redirect(method = "finalizeBook", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;map(Ljava/util/function/Function;)Ljava/util/stream/Stream;"))
    public Stream<NbtString> onMapSave(Stream<String> stream, Function<String, NbtString> mapper) {
        return stream.map((s) -> {
            return MessageUtils.replaceColorCodes('&', s);
        }).map(mapper);
    }*/
}
