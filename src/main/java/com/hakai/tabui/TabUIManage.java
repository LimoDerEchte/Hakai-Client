package com.hakai.tabui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabUIManage {
    public static HashMap<String, Integer> selected = new HashMap<>();
    private List<TabUIElement> elements = new ArrayList<TabUIElement>();
    public String title = "Error";
    public String currentMenu = "main";
    public String parentMenu = "main";
    public static TabUIManage last = new TabUIManage("main");

    public static KeyBinding up, down, interact, back;

    public TabUIManage(String currentMenu) {
        last = this;
        this.currentMenu = currentMenu;
        if(!selected.containsKey(currentMenu))
            selected.put(currentMenu, 0);
    }

    // Elements Begin
    public void Title(String title){
        this.title = title;
    }
    public void Parent(String parentUi){
        this.parentMenu = parentUi;
    }
    public void Text(String text){
        Text(text, false);
    }
    public void Text(String text, boolean highlight){
        elements.add(new TabUIElement(text, null, highlight));
    }
    public void Clickable(String text, Runnable run){
        Clickable(text, run, false);
    }
    public void Clickable(String text, Runnable run, boolean highlight){
        elements.add(new TabUIElement(text, run, highlight));
    }
    public void Clickable(String text, boolean highlight, String command){
        Clickable(text, () -> {
            MinecraftClient.getInstance().player.sendChatMessage(command);
        }, highlight);
    }
    public void Submenu(String text, String menu){
        Clickable(text, new Runnable() {
            @Override
            public void run() {
                TabUI.setUI(menu);
            }
        });
    }
    public void Submenu(String text, String menu, String... args){
        Clickable(text, new Runnable() {
            @Override
            public void run() {
                TabUI.setUI(menu);
                TabUI.menuArgs = args;
            }
        });
    }
    // Elements End

    // Logic Begin
    public void checkControls(){
        int newselect = selected.get(currentMenu);
        if(down.wasPressed() && newselect < elements.size() - 1)
            newselect++;
        else if(up.wasPressed() && newselect > 0)
            newselect--;

        else if(interact.wasPressed())
            elements.get(newselect).click();
        else if(back.wasPressed())
            TabUI.setUI(parentMenu);
        selected.put(currentMenu, newselect);
    }
    public List<TabUIElement> getElements() {
        return elements;
    }
    public int getSelected() {
        return selected.get(currentMenu);
    }
    // Logic End

    public record TabUIElement(String text, Runnable run, boolean highlight){
        public void click(){
            if(run != null) run.run();
        }
    }

    public static void init(){
        up = new KeyBinding("TabUI Up", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_UP, "ysc.tabui");
        down = new KeyBinding("TabUI Down", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_DOWN, "ysc.tabui");
        interact = new KeyBinding("TabUI Interact", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_ENTER, "ysc.tabui");
        back = new KeyBinding("TabUI Back", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_BACKSPACE, "ysc.tabui");
    }
}
