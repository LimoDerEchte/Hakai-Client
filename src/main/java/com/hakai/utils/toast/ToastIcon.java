package com.hakai.utils.toast;

import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.item.ItemStack;

public class ToastIcon {
    public static final ToastIcon WARNING = new ToastIcon(SystemToast.Type.WORLD_ACCESS_FAILURE);
    public static final ToastIcon KEYBOARD = new ToastIcon(TutorialToast.Type.MOVEMENT_KEYS);
    public static final ToastIcon MOUSE = new ToastIcon(TutorialToast.Type.MOUSE);
    public static final ToastIcon TREE = new ToastIcon(TutorialToast.Type.TREE);

    private final Object object;

    protected ToastIcon(Object object) {
        this.object = object;
    }

    protected final Object getObject() {
        return object;
    }

    public static ToastIcon fromItemStack(ItemStack itemStack) {
        return new ToastIcon(itemStack);
    }

}
