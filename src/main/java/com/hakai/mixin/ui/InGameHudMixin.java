package com.hakai.mixin.ui;

import com.hakai.main.HakaiClient;
import com.hakai.tabui.TabUI;
import com.hakai.tabui.TabUIManage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

@Mixin(value = InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void clear();

    @Shadow public abstract TextRenderer getTextRenderer();

    private float scale = 1;
//
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getTextRenderer()Lnet/minecraft/client/font/TextRenderer;", ordinal = 0))//"HEAD"))
    private void onRender(MatrixStack matrix, float tickDelta, CallbackInfo info) {
        /*scale(matrix,1.5f);
        int y = 6;
        String display = "Hakai Client";
        this.client.textRenderer.draw(matrix, display, 9, y, HakaiClient.getInstance().getRGB());
        normalizeScale(matrix);
        this.client.textRenderer.draw(matrix, HakaiClient.VERSION, (int) (this.client.textRenderer.getWidth(display)*1.5) + 30, y * (float)1.5, HakaiClient.getInstance().getRGB());
        y += (int) (this.client.textRenderer.fontHeight * 1.5) + 5;
        DrawableHelper.fill(matrix, 5, y, (int) (this.client.textRenderer.getWidth(display)*1.5) + 15, y+1, Color.DARK_GRAY.getRGB());
        y += 5;
        List<Supplier<String>> elementList = HakaiClient.getInstance().getHudElements();

        String elementString;
        for(Supplier<String> supplierElement : elementList) {
            elementString = supplierElement.get();
            if(elementString != null)
                for(String line : elementString.split("\n")) {
                    this.client.textRenderer.draw(matrix, line, 12, y, HakaiClient.getInstance().getRGB());
                    y += this.client.textRenderer.fontHeight + 2;
                }
        }*/

        HakaiClient.tabUI.Update();
        Color rgb = new Color(TabUI.uiColor);
        Color textRgb = new Color(TabUI.textColor);
        if(TabUI.uiRgb) rgb = new Color(HakaiClient.getInstance().getRGB());
        if(TabUI.textRgb) textRgb = new Color(HakaiClient.getInstance().getRGB());

        Color alpha1 = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 30);
        Color alpha2 = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 50);
        Color alpha3 = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 75);
        Color alpha4 = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 90);

        DrawableHelper.fill(matrix, 10, 10, 200, 30, alpha4.getRGB());
        this.client.textRenderer.draw(matrix, TabUIManage.last.title, 100 - this.client.textRenderer.getWidth(TabUIManage.last.title) / 2, 16, textRgb.getRGB());

        int id = 0;
        for(TabUIManage.TabUIElement element : TabUIManage.last.getElements()){
            if(id == TabUIManage.last.getSelected())
                DrawableHelper.fill(matrix, 10, 30 + id * 20, 200, 50 + id * 20, alpha3.getRGB());
            else if(element.highlight())
                DrawableHelper.fill(matrix, 10, 30 + id * 20, 200, 50 + id * 20, alpha2.getRGB());
            else
                DrawableHelper.fill(matrix, 10, 30 + id * 20, 200, 50 + id * 20, alpha1.getRGB());
            this.client.textRenderer.draw(matrix, element.text(), 20, 36 + id * 20, textRgb.getRGB());
            id++;
        }
    }

    private void scale(MatrixStack matrix, float f) {
        if(f <= 1)
            return;
        if(scale != 1)
            normalizeScale(matrix);
        scale = f;
        matrix.scale(f,f,f);
    }

    private void normalizeScale(MatrixStack matrix) {
        float unscale = 1 / scale;
        matrix.scale(unscale,unscale,unscale);
        scale = 1;
    }

}
