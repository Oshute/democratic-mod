package com.democratic.mod.mixin;

import com.democratic.mod.CommandInterceptor;
import com.democratic.mod.VoteManager;
import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin qui intercepte l'exécution des commandes.
 * Si la commande est protégée, elle lance un vote au lieu de s'exécuter.
 */
@Mixin(CommandManager.class)
public class CommandManagerMixin {

    @Inject(
        method = "executeWithPrefix",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onCommandExecute(ServerCommandSource source, String command, CallbackInfoReturnable<Integer> cir) {
        // Vérifie que c'est bien un joueur (pas la console)
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) return;

        // Vérifie si la commande est protégée
        if (!CommandInterceptor.isProtectedCommand(command)) return;

        // Ne pas intercepter les commandes /voteyes, /voteno, /votestatus
        String lower = command.toLowerCase().trim();
        if (lower.startsWith("/")) lower = lower.substring(1);
        if (lower.startsWith("voteyes") || lower.startsWith("voteno") || lower.startsWith("votestatus")) return;

        // Annuler l'exécution originale
        cir.setReturnValue(0);

        // Lancer le vote
        String description = CommandInterceptor.getCommandDescription(command);
        boolean started = VoteManager.getInstance().startVote(
            source.getServer(),
            player,
            command,
            description
        );

        if (!started) {
            player.sendMessage(Text.literal("§c[Vote] Impossible de lancer le vote. Réessaie plus tard."), false);
        }
    }
}
