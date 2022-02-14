package com.hakai.gui;

import com.hakai.main.HakaiClient;
import com.hakai.tabui.TabUI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public class CleanUp extends Screen {
    private static String state = "Waiting...";
    private static int mode = 0;
    private static boolean filter = false;

    private MultiplayerScreen prevscreen;
    private ButtonWidget modeButton;
    private ButtonWidget filterButton;

    public CleanUp(MultiplayerScreen prevscreen) {
        super(new LiteralText("Cleanup"));
        this.prevscreen = prevscreen;
    }

    @Override
    protected void init() {
        super.init();
        modeButton = this.addDrawableChild(new ButtonWidget(width / 2 - 100, height / 2 - 50, 200, 20, new LiteralText(mode == 0 ? "Mode: Alle Server" : mode == 1 ? "Mode: Nur gelbe & rote Server" : "Mode: Nur rote Server"), new ButtonWidget.PressAction() {
            @Override
            public void onPress(ButtonWidget button) {
                mode++;
                if(mode > 2) mode = 0;
            }
        }));
        filterButton = this.addDrawableChild(new ButtonWidget(width / 2 - 100, height / 2 - 25, 200, 20, new LiteralText("Hakai# Server werden " + (filter ? "" : "nicht ") + "gefiltert"), new ButtonWidget.PressAction() {
            @Override
            public void onPress(ButtonWidget button) {
                filter = !filter;
            }
        }));
        this.addDrawableChild(new ButtonWidget(width / 2 - 100, height / 2, 200, 20, new LiteralText("Cleanup"), new ButtonWidget.PressAction() {
            @Override
            public void onPress(ButtonWidget button) {
                Thread t = new Thread(() -> {
                    cleanup();
                });
                t.start();
            }
        }));
    }

    @Override
    public void tick() {
        super.tick();
        modeButton.setMessage(new LiteralText(mode == 0 ? "Mode: Alle Server" : mode == 1 ? "Mode: Nur gelbe & rote Server" : "Mode: Nur rote Server"));
        filterButton.setMessage(new LiteralText("Hakai# Server werden " + (filter ? "" : "nicht ") + "gefiltert"));
    }

    private void cleanup() {
        state = "Filtering...";
        List<ServerInfo> toRemove = new ArrayList<>();
        for(int i = 0; i < prevscreen.getServerList().size(); i++)
            if(check(prevscreen.getServerList().get(i)))
                toRemove.add(prevscreen.getServerList().get(i));
        state = "Removing...";
        for(ServerInfo server : toRemove)
            prevscreen.getServerList().remove(server);
        state = "Finished";
        prevscreen.getServerList().saveFile();
    }

    private boolean check(ServerInfo server) {
        MultiplayerServerListPinger ping = new MultiplayerServerListPinger();
        try {
            ping.add(server, () -> {});
        } catch (Exception e) {
            return true;
        }
        if(filter && !server.name.startsWith("Hakai#"))
            return false;
        if(mode == 0)
            return true;
        if(mode == 1 && !(server.version.getString().contains("spigot") || server.version.getString().contains("bukkit") || server.version.getString().contains("bungeecord") || server.version.getString().split(" ").length == 1))
            return true;
        if(mode == 2 && !(server.version.getString().contains("vanilla") || server.version.getString().contains("spigot") || server.version.getString().contains("bukkit") || server.version.getString().contains("bungeecord") || server.version.getString().split(" ").length == 1))
            return true;
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        textRenderer.draw(matrices, "Status: " + state, width / 2 - 100, height / 4 + 140, TabUI.textRgb ? HakaiClient.getInstance().getRGB() : TabUI.textColor);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
