package com.hakai.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.hakai.command.CommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestor.class)
public abstract class CommandSuggestorMixin {

    @Shadow private ParseResults<CommandSource> parse;

    @Shadow @Final private TextFieldWidget textField;

    @Shadow @Final private MinecraftClient client;

    @Shadow private boolean completingSuggestions;

    @Shadow private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow protected abstract void show();

    @Shadow private CommandSuggestor.SuggestionWindow window;

    @Inject(method = "refresh",
            at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void onRefresh(CallbackInfo ci, String string, StringReader reader) {
        if (reader.canRead(1) && reader.getString().startsWith(String.valueOf(CommandManager.COMMAND_PREFIX), reader.getCursor())) {
            reader.setCursor(reader.getCursor() + 1);
            assert this.client.player != null;
            // Pretty much copy&paste from the refresh method
            CommandDispatcher<CommandSource> commandDispatcher = CommandManager.get().getDispatcher();
            if (this.parse == null) {
                this.parse = commandDispatcher.parse(reader, CommandManager.get().getCommandSource());
            }

            int cursor = textField.getCursor();
            if (cursor >= 1 && (this.window == null || !this.completingSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.show();
                    }
                });
            }
            ci.cancel();
        }
    }

}
