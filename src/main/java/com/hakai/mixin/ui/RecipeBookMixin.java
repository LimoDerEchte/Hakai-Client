package com.hakai.mixin.ui;

import com.hakai.commands.Crash;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.Recipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecipeBookResults.class)
public class RecipeBookMixin {

    @Shadow @Nullable private RecipeResultCollection resultCollection;

    @Shadow @Nullable private Recipe<?> lastClickedRecipe;

    @Inject(method = "getLastClickedRecipe", at = @At("TAIL"))
    public void getLastClickedRecipe(CallbackInfoReturnable<Recipe<?>> cir) {
        if(lastClickedRecipe != null) Crash.clickedRecipe(lastClickedRecipe);
    }
}
