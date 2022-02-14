package com.hakai.utils;

import com.ibasco.image.gif.GifFrame;
import com.ibasco.image.gif.GifImageReader;
import com.hakai.irc.client.IRCClient;
import com.hakai.irc.protocol.packets.capes.IRCPacketGetCape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CapeManager {
    public final UUID uuid;
    private final ArrayList<Identifier> capeTexture = new ArrayList<>();
    private boolean animated = false;
    private int animationSpeed = 0;
    private int ticksLeft = 0;
    private int currentTexture = 0;
    private int timeWithoutRefresh = 0;
    public String capeId = "";

    private static int fullRefresh = 0;
    private static HashMap<UUID, CapeManager> instances = new HashMap<>();

    public CapeManager(UUID id){
        this.uuid = id;
        this.refresh();
    }

    public static CapeManager fromProfile(UUID id){
        if(!instances.containsKey(id)) instances.put(id, new CapeManager(id));
        return instances.get(id);
    }

    public Identifier getTexture(){
        timeWithoutRefresh = 0;
        if(currentTexture >= capeTexture.size()) currentTexture = 0;
        if(capeTexture.size() <= 0) return null;
        return capeTexture.get(currentTexture);
    }

    public static void handleCapePacket(UUID user, String capeId, String capeUrl, String filename, int animationSpeed){
        final CapeManager manager = fromProfile(user);
        if(manager.capeId.equals(capeId)) return;

        manager.clearTextures();
        if(capeId.equals("None")) return;
        Thread thread = new Thread(() -> {
            File file = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "yellowsnow/capes/" + filename);
            if(!file.exists()){
                try {
                    file.getParentFile().mkdirs();

                    URLConnection con = new URL(capeUrl).openConnection();
                    con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0");
                    InputStream in = con.getInputStream();
                    Files.copy(in, file.toPath());
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            manager.setCape(file.getAbsolutePath(), animationSpeed);
        });
        thread.start();
    }

    private void setCape(String path, int ticksPerFrame) {
        BufferedImage img;
        //if(ticksPerFrame > 0) {
            this.animated = true;
            this.animationSpeed = ticksPerFrame;

            try {
                File file = new File(path);
                GifImageReader reader = new GifImageReader(file);

                boolean firstRun = true;
                NativeImage lastImage = null;

                while (reader.hasRemaining()){
                    GifFrame frame = reader.read();

                    NativeImage image = null;
                    if(lastImage != null){
                        image = new NativeImage(lastImage.getWidth(), lastImage.getHeight(), true);
                    }else
                        image = new NativeImage(frame.getWidth(), frame.getHeight(), true);

                    for(int y = 0; y < frame.getHeight(); y++){
                        for(int x = 0; x < frame.getWidth(); x++){
                            if(frame.getData()[y * frame.getWidth() + x] != 0){
                                Color c = new Color(frame.getData()[y * frame.getWidth() + x], false);
                                image.setColor(x, y, new Color(c.getBlue(), c.getGreen(), c.getRed(), c.getAlpha()).getRGB());
                            }else {
                                if(lastImage != null) image.setColor(x, y, lastImage.getColor(x, y));
                                else image.setColor(x, y, new Color(0, 0, 0, 0).getRGB());
                            }
                        }
                    }

                    addFrame(image);
                    lastImage = image;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        /*}
        else {
            try {
                NativeImage img2 = NativeImage.
                addFrame(img2);
            } catch (IOException e) {
                System.out.println("Cape Texture not found!");
            }
        }*/
    }

    private void addFrame(BufferedImage img){
        int imageWidth = 64;
        int imageHeight = 32;
        int srcWidth = img.getWidth();
        int srcHeight= img.getHeight();

        while (imageWidth < srcWidth || imageHeight < srcHeight) {
            imageWidth *= 2;
            imageHeight *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < srcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                Color c = new Color(img.getRGB(x, y));
                imgNew.setColor(x, y, new Color(c.getBlue(), c.getGreen(), c.getRed(), c.getAlpha()).getRGB());
            }
        }

        addFrame(imgNew);
    }

    private void addFrame(NativeImage img){
        MinecraftClient.getInstance().submit(() -> {
            this.capeTexture.add(MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(uuid.toString().replace("-", ""), new NativeImageBackedTexture(img)));
        });
    }

    public void refresh(){
        clearTextures();
        IRCClient.get().sendPacket(new IRCPacketGetCape(this.uuid));
    }

    public void update(){
        if(animated){
            ticksLeft--;
            if(ticksLeft <= 0){
                ticksLeft = animationSpeed;
                currentTexture++;
                if(currentTexture >= capeTexture.size()){
                    currentTexture = 0;
                }
            }
        }
        timeWithoutRefresh++;
        if(timeWithoutRefresh >= 1000) instances.remove(this);
    }

    public void clearTextures(){
        capeTexture.clear();
    }

    public static void updateAllCapes(){
        instances.forEach((uuid, capeManager) -> {
            capeManager.refresh();
        });
    }

    public static void tick(){
        fullRefresh++;
        if(fullRefresh >= 600) {
            fullRefresh = 0;
            updateAllCapes();
        }
        instances.forEach((uuid, capeManager) -> {
            capeManager.update();
        });
    }
}
