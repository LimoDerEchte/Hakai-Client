package com.hakai.mixin.ui;

import com.hakai.gui.CleanUp;
import com.hakai.gui.ServerFinder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    @Shadow private ServerInfo selectedEntry;

    @Shadow protected abstract void addEntry(boolean confirmedAction);

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci){
        addDrawableChild(new ButtonWidget(width / 2 + 8 + 150, height - 52, 100, 20, new LiteralText("Server Finder"), b -> client.setScreen(new ServerFinder((MultiplayerScreen)(Object)this))));
        addDrawableChild(new ButtonWidget(width / 2 + 154 + 4, height - 28, 100, 20, new LiteralText("Clean Up"), b -> client.setScreen(new CleanUp((MultiplayerScreen)(Object)this))));
    }
}
