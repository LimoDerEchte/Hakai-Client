package com.hakai.utils.toast.internal;

import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class TimedTutorialToast extends TutorialToast {
    private long startTime;
    private boolean firstDraw;

    public TimedTutorialToast(Type type, Text title, @Nullable Text description, boolean hasProgressBar) {
        super(type, title, description, hasProgressBar);
        firstDraw = true;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (firstDraw) {
            this.startTime = startTime;
            firstDraw = false;
        }

        return startTime - this.startTime < 5000L ?
                super.draw(matrices, manager, startTime) : Toast.Visibility.HIDE;
    }

}
