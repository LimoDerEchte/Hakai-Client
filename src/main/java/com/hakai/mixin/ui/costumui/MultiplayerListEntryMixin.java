package com.hakai.mixin.ui.costumui;

import com.hakai.main.HakaiClient;
import com.hakai.tabui.TabUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultiplayerServerListWidget.ServerEntry.class)
public class MultiplayerListEntryMixin {
    @Shadow @Final private ServerInfo server;

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if(this.server.ping > 0) {
            String versionString = versionContains("vanilla") || server.version.getString().split(" ").length == 1 ? "§a" : versionContains("spigot") || versionContains("bukkit")  || versionContains("bungeecord") || server.version.getString().split(" ").length == 1 ? "§e" : "§c";
            versionString += this.server.version.getString();
            MinecraftClient.getInstance().textRenderer.draw(matrices, Text.of("Version: " + versionString/* + " (" + server.protocolVersion + ")"*/), (float)(x + entryWidth + 12), (float)(y + 1), TabUI.textRgb ? HakaiClient.getInstance().getRGB() : TabUI.textColor);
            MinecraftClient.getInstance().textRenderer.draw(matrices, Text.of("Ping: " + server.ping), (float)(x + entryWidth + 12), (float)(y + 10), TabUI.textRgb ? HakaiClient.getInstance().getRGB() : TabUI.textColor);
        }
    }

    private boolean versionContains(String sequence) {
        return this.server.version.getString().toLowerCase().contains(sequence.toLowerCase());
    }
}
