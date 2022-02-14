package com.hakai.mixin.client;

import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ClientBrandRetriever.class)
public class ClientBrandRetrieverMixin {

    /**
     * @author

    @Overwrite
    public static String getClientModName() {
        return "vanilla";
    }*/

}
