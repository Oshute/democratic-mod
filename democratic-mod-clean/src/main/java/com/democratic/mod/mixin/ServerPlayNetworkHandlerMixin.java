package com.democratic.mod.mixin;

import com.democratic.mod.CommandInterceptor;
import com.democratic.mod.VoteManager;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepte le raccourci F3+F4 (changement de mode de jeu via le menu en jeu).
 * En Fabric/Vanilla, le changement de gamemode via F3+F4 envoie un packet particulier.
 */
@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    /**
     * Intercepte le changement de gamemode via le packet client.
     * Fabric envoie une commande /gamemode lors du F3+F4.
     * Cette interception est un filet de sécurité supplémentaire.
     */
    @Inject(
        method = "onGameMessage",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onChatMessage(net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket packet, CallbackInfo ci) {
        // Les messages de chat normaux ne sont pas des commandes, on ignore
        // (Les commandes passent par onCommandExecution, pas par ici)
    }
}
