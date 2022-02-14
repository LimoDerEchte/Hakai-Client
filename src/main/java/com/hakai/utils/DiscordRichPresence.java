package com.hakai.utils;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.minecraft.client.MinecraftClient;

import java.sql.Timestamp;

public class DiscordRichPresence {
    public long startTimeStamp;
    public String text;
    public boolean running;

    public DiscordRichPresence(String text) {
        this.text = text;
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
        }).build();
        DiscordRPC.discordInitialize("942372662506819655", handlers, true);
    }

    public void start(){
        startTimeStamp = new Timestamp(System.currentTimeMillis()).getTime();
        updatePresence(text);
        running = true;
    }

    public void stop(){
        DiscordRPC.discordClearPresence();
        running = false;
    }

    public void updatePresence(String text) {
        this.text = text;
        net.arikia.dev.drpc.DiscordRichPresence discordPresence = new net.arikia.dev.drpc.DiscordRichPresence();
        discordPresence.state = text;
        discordPresence.details = "Hacking and stuff you know?";
        discordPresence.startTimestamp = startTimeStamp;
        discordPresence.largeImageKey = "hakai-trans-bg";
        discordPresence.largeImageText = "https://hakai-team.de/";
        DiscordRPC.discordUpdatePresence(discordPresence);
    }

    public void tick(){
        if(!running) return;
        if(MinecraftClient.getInstance().getNetworkHandler() != null) {
            String text = MinecraftClient.getInstance().getNetworkHandler().getConnection().getAddress().toString();
            if (!text.equals(this.text))
                updatePresence(text.contains("local") ? "Singleplayer" : text.split("/")[1]);
        } else {
            if (!text.equals("Main Menu"))
                updatePresence("Main Menu");
        }
    }
}
