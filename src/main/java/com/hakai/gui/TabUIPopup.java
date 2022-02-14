package com.hakai.gui;

import com.hakai.main.HakaiClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class TabUIPopup extends Screen {
    private TextFieldWidget input;
    private final Consumer<String> output;
    
    public TabUIPopup(String title, Consumer<String> output) {
        super(Text.of(title));
        this.output = output;
        MinecraftClient.getInstance().setScreen(this);
    }

    @Override
    protected void init() {
        input = this.addDrawableChild(new TextFieldWidget(this.textRenderer, this.width / 2 - 75, this.height / 2 - 10, 150, 20, Text.of("")));

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, this.height / 2 + 30, 100, 20, Text.of("Submit"), (buttonWidget) -> {
            output.accept(input.getText());
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.textRenderer.draw(matrices, this.title, this.width / 2, this.height / 2 - 50, HakaiClient.getInstance().getRGB());
    }
}
