package com.hakai.mixin.client;

import com.hakai.gui.BungeeGui;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HandshakeC2SPacket.class)
public class HandshakePacketMixin {
    @Mutable @Shadow @Final private String address;

    @Redirect(method = "<init>(Ljava/lang/String;ILnet/minecraft/network/NetworkState;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/network/packet/c2s/handshake/HandshakeC2SPacket;address:Ljava/lang/String;", opcode = Opcodes.PUTFIELD))
    public void onAddressChange(HandshakeC2SPacket instance, String value){
        if(BungeeGui.INSTANCE.isActive()){
            address = value;
        }else {
            address = BungeeGui.INSTANCE.getBungeeHost(value);
        }
    }
}
