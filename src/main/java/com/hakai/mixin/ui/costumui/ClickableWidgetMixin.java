package com.hakai.mixin.ui.costumui;

import com.hakai.main.HakaiClient;
import com.hakai.tabui.TabUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = ClickableWidget.class)
public abstract class ClickableWidgetMixin extends DrawableHelper {
    @Shadow public abstract Text getMessage();
    @Shadow public int x, y;
    @Shadow protected int width, height;
    @Shadow protected float alpha;
    @Shadow public boolean active, hovered, visible;

    @Shadow protected abstract void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY);

    public int fade = 100;

    /**
     * @author
     */
    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
        ci.cancel();

        if(!visible) return;
        hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        fading(hovered);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        Color rgb = new Color(TabUI.uiColor);
        Color textRgb = new Color(TabUI.textColor);
        if(TabUI.uiRgb) rgb = new Color(HakaiClient.getInstance().getRGB());
        if(TabUI.textRgb) textRgb = new Color(HakaiClient.getInstance().getRGB());
        Color alpha = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), fade);

        fill(matrices, x, y, x + width, y + height, alpha.getRGB());
        drawCenteredText(matrices, textRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, textRgb.getRGB());
        renderBackground(matrices, minecraftClient, mouseX, mouseY);
    }

    public void fading(boolean hovering) {
        if(hovering && fade < 160) fade = (int) (fade * 1.05);
        if(!hovering && fade > 100) fade = (int) (fade / 1.05);
    }
}
