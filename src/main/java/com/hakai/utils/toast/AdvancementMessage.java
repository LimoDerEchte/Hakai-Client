package com.hakai.utils.toast;

import com.hakai.utils.toast.internal.TimedTutorialToast;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.*;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class AdvancementMessage {

    public static void displayMessage(@NotNull Text titleText, @Nullable Text messageText,
                                      @NotNull ToastIcon icon) {
        Validate.notNull(icon, "Icon cannot be null");
        Validate.notNull(titleText, "Title cannot be null");

        MinecraftClient client = MinecraftClient.getInstance();
        ToastManager manager = client.getToastManager();

        Toast toast = null;

        Object iconObject = icon.getObject();
        Validate.notNull(iconObject, "Icon#getObject() cannot return null");

        if (iconObject instanceof SystemToast.Type) {
            SystemToast.Type type = (SystemToast.Type) iconObject;

            toast = new SystemToast(type, titleText, messageText);
        } else if (iconObject instanceof TutorialToast.Type) {
            TutorialToast.Type type = (TutorialToast.Type) iconObject;

            toast = new TimedTutorialToast(type, titleText, messageText, false);
        } else if (iconObject instanceof ItemStack) {
            ItemStack iconItem = (ItemStack) iconObject;

            AdvancementDisplay display = new AdvancementDisplay(iconItem, titleText, messageText,
                    null, AdvancementFrame.TASK, true, false, false);
            Advancement advancement = new Advancement(null, null, display,
                    null, Collections.emptyMap(), null);

            toast = new AdvancementToast(advancement);
        }

        if (toast == null)
            throw new IllegalStateException("No toast found for the specified icon");
        manager.add(toast);
    }

}
