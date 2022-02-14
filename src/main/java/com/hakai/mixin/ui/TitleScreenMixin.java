package com.hakai.mixin.ui;

import com.hakai.gui.altlogin.AltLogin;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 96 /*48 + 72 + 12 + 24*/, 98, 20, Text.of("Alt Login"), (buttonWidget) -> {
            this.client.setScreen(new AltLogin());
        }, ButtonWidget.EMPTY));

        ButtonWidget realmsButton = getRealmsButton();
        if(realmsButton != null) {
            realmsButton.setMessage(Text.of("Realms"));
            realmsButton.setWidth(98);
            realmsButton.x = this.width / 2 - 100;
        }
    }

    private ButtonWidget getRealmsButton() {
        for(Element e : this.children()) {
            if(e instanceof  ButtonWidget && ((ButtonWidget) e).getMessage() instanceof TranslatableText && ((TranslatableText) ((ButtonWidget) e).getMessage()).getKey().equals("menu.online")) {
                return (ButtonWidget) e;
            }
        }
        return null;
    }

}
