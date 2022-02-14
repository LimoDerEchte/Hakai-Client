package com.hakai.tabui;

import com.google.gson.JsonObject;
import com.hakai.commands.*;
import com.hakai.utils.config.SubConfig;
import com.hakai.utils.config.YellowSnowConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.hakai.utils.MessageUtils;

import java.awt.*;

public class TabUI implements SubConfig {
    private static String selected = "main";

    public static boolean textRgb;
    public static boolean uiRgb;
    public static int textColor = Color.RED.getRGB();
    public static int uiColor = Color.RED.getRGB();

    public static String[] menuArgs;

    public TabUI() {
        YellowSnowConfig.get().registerSubConfig("TabUI", this);
    }

    public void Update(){
        TabUIManage ui = new TabUIManage(selected);

        switch (selected) {
            case "main":
                ui.Title("YellowSnow Client");
                ui.Submenu("Movement", "movement");
                ui.Submenu("Rendering", "render");
                ui.Submenu("World", "world");
                ui.Submenu("Exploits", "exploit");
                ui.Submenu("Misc", "misc");
                ui.Submenu("Einstellungen", "settings");
                break;


            // Movement (Fly)
            case "movement":
                ui.Title("Movement");
                ui.Clickable("Fly", () -> {
                    MessageUtils.printChatMessageWithPrefix("§cIn Arbeit...");
                });
                break;


            // Render (Fullbright, XRay)
            case "render":
                ui.Title("Rendering");
                ui.Clickable("Fullbright", Fullbright.isActive(), "#fullbright");
                break;


            // World (Holograms, Fastplace)
            case "world":
                ui.Title("World");
                ui.Clickable("Fastplace", FastplaceCommand.isActive(), "#fastplace");
                break;


            // Exploit (Dupe, Crash, Kick)
            case "exploit":
                ui.Title("Exploits");
                ui.Submenu("Dupes", "dupe");
                ui.Submenu("Crashes", "crash");
                ui.Submenu("Kick Exploits", "kick");
                ui.Submenu("Spawn Exploits", "spawn");
                break;

            case "dupe":
                ui.Title("Dupes");
                ui.Parent("exploit");
                ui.Clickable("Shulker Dupe", Dupe.shulkerDupe, "#dupe shulker");
                ui.Clickable("Random Buch", false, "#dupe randombook");
                ui.Clickable("Durchrasten 2FA", true, "#dupe 2fa-map");
                break;

            case "crash":
                ui.Title("Crashes");
                ui.Parent("exploit");
                ui.Clickable("Cic-DDOS", false, "#crash crea-ddos 500");
                ui.Clickable("Firework", false, "#crash firework");
                ui.Clickable("nocom", false, "#crash nocom 1000");
                ui.Clickable("/friend add", Crash.friend, "#crash friend-add");
                ui.Clickable("PermissionsEX", false, "#crash pex 50");
                ui.Clickable("Vehicle Spam", Crash.rideFucker, "#crash vehiclespam");
                ui.Clickable("WorldEdit //calc", false, "#crash worldedit");
                ui.Clickable("Craft-Crash", Crash.nonsense, "#crash craft 1000");
                break;

            case "kick":
                ui.Title("Kick Exploits");
                ui.Parent("exploit");
                ui.Clickable("Cic-Egg", false, "#kick cic-egg");
                break;

            case "spawn":
                ui.Title("Spawn Exploits");
                ui.Parent("exploit");
                ui.Clickable("Hologramme", () -> {
                    MessageUtils.printChatMessageWithPrefix("§aAlle Hologramm basierte Commands:");
                    MessageUtils.printChatMessageWithPrefix("§e#hologram <text>");
                    MessageUtils.printChatMessageWithPrefix("§e#imgholo <cancel|url> [width]");
                    MessageUtils.printChatMessageWithPrefix("§e#structholo <cancel|hs|schematic> [name]");
                });
                break;


            // Andere (Serverinfo, Plugindetector)
            case "misc":
                ui.Title("Andere");
                ui.Clickable("Serverinfo", false, "#server");
                ui.Clickable("InstaKill Potion", false, "#pvp instakill-potion");
                ui.Clickable("Bow InstaKill", PvPCommand.bowInstaKill, "#pvp bowinstakill");
                break;


            // Einstellungen (Main, Themes)
            case "settings":
                ui.Title("Einstellungen");
                ui.Submenu("Theme", "theme");
                break;

            case "theme":
                ui.Title("Theme");
                ui.Parent("settings");
                ui.Submenu("Text-Farbe", "colorselect", "theme", "text");
                ui.Clickable("Rainbow-Text", () -> {
                    textRgb = !textRgb;
                }, textRgb);
                ui.Submenu("Hintergrund-Farbe", "colorselect", "theme", "ui");
                ui.Clickable("Rainbow-Hintergrund", () -> {
                    uiRgb = !uiRgb;
                }, uiRgb);
                break;

            case "colorselect":
                ui.Title("Farbauswahl");
                if(menuArgs.length == 2){
                    ui.Parent(menuArgs[0]);
                    ColorSelect(ui, "Rot", Color.red.getRGB());
                    ColorSelect(ui, "Grün", Color.green.getRGB());
                    ColorSelect(ui, "Blau", Color.blue.getRGB());
                    ColorSelect(ui, "Schwarz", Color.black.getRGB());
                    ColorSelect(ui, "Weiß", Color.white.getRGB());
                }else{
                    ui.Text("Ein Fehler ist aufgetreten!");
                }
                break;

            default:
                MessageUtils.printChatMessageWithPrefix("§cUngültiges Menu: " + selected + "!");
                selected = "main";
        }
        ui.checkControls();
    }

    public static void setUI(String ui){
        selected = ui;
    }

    public static void init(){
        selected = "main";
        TabUIManage.init();
    }

    public void ColorSelect(TabUIManage ui, String text, int color){
        ui.Clickable(text, new Runnable() {
            @Override
            public void run() {
                switch (menuArgs[1]){
                    case "text":
                        textColor = color;
                        break;
                    case "ui":
                        uiColor = color;
                        break;
                }
            }
        });
    }

    @Override
    public boolean load(JsonObject element) {
        boolean complete = true;
        if(element.has("uiBackground"))
            uiColor = element.get("uiBackground").getAsInt();
        else
            complete = false;
        if(element.has("uiText"))
            textColor = element.get("uiText").getAsInt();
        else
            complete = false;
        if(element.has("uiBgRgb"))
            uiRgb = element.get("uiBgRgb").getAsBoolean();
        else
            complete = false;
        if(element.has("uiTextRgb"))
            textRgb = element.get("uiTextRgb").getAsBoolean();
        else
            complete = false;

        return complete;
    }

    @Override
    public void save(JsonObject element) {
        element.addProperty("uiBackground", uiColor);
        element.addProperty("uiText", textColor);
        element.addProperty("uiBgRgb", uiRgb);
        element.addProperty("uiTextRgb", textRgb);
    }
}
