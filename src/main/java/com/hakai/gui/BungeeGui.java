package com.hakai.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class BungeeGui extends Screen {

    public static final BungeeGui INSTANCE = new BungeeGui();

    private boolean lastToggle = true;

    private ToggleButtonWidget toggle;
    private TextFieldWidget username;
    private TextFieldWidget uuid;
    private TextFieldWidget hostName;
    private TextFieldWidget fakeIp;

    public BungeeGui() {
        super(Text.of("Bungee Exploit"));
    }

    public boolean isActive() {
        if(toggle == null)
            return false;
        return toggle.isToggled();
    }

    public String getBungeeHost(String host) {
        return "";
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(toggle = new ToggleButtonWidget(20, 20, 40, 40, false));

        addDrawableChild(fakeIp = new TextFieldWidget(this.textRenderer, this.width / 2 - 265, this.height / 2 - 40, 250, 20, Text.of("Fake IP")));
        addDrawableChild(hostName = new TextFieldWidget(this.textRenderer, this.width / 2 + 15, this.height / 2 - 40, 250, 20, Text.of("Fake Host")));
        addDrawableChild(username = new TextFieldWidget(this.textRenderer, this.width / 2 - 265, this.height / 2, 250, 20, Text.of("Fake Username")));
        addDrawableChild(uuid = new TextFieldWidget(this.textRenderer, this.width / 2 + 15, this.height / 2, 250, 20, Text.of("Fake UUID")));

    }

    @Override
    public void tick() {
        username.setEditable(toggle.isToggled());
        uuid.setEditable(toggle.isToggled());
        hostName.setEditable(toggle.isToggled());
        fakeIp.setEditable(toggle.isToggled());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

}
