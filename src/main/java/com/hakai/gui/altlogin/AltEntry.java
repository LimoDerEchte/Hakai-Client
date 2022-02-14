package com.hakai.gui.altlogin;

import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;

public class AltEntry extends EntryListWidget.Entry {
    public final AccountType type;

    public AltEntry(AccountType type){

        this.type = type;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

    }

    public enum AccountType{
        MOJANG,
        MICROSOFT,
        CRACKED
    }
}
