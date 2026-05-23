package com.democratic.mod;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Enregistre les commandes /voteyes et /voteno
 */
public class VoteCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        // /voteyes
        dispatcher.register(
            CommandManager.literal("voteyes")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    VoteManager.getInstance().castVote(ctx.getSource().getServer(), player, true);
                    return 1;
                })
        );

        // /voteno
        dispatcher.register(
            CommandManager.literal("voteno")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    VoteManager.getInstance().castVote(ctx.getSource().getServer(), player, false);
                    return 1;
                })
        );

        // /votestatus - voir les votes en cours
        dispatcher.register(
            CommandManager.literal("votestatus")
                .executes(ctx -> {
                    ServerCommandSource source = ctx.getSource();
                    var sessions = VoteManager.getInstance().getActiveSessions();

                    if (sessions.isEmpty()) {
                        source.sendMessage(net.minecraft.text.Text.literal("§7[Vote] Aucun vote en cours."));
                        return 1;
                    }

                    for (var entry : sessions.entrySet()) {
                        VoteSession s = entry.getValue();
                        source.sendMessage(net.minecraft.text.Text.literal(
                            "§e[Vote] §f" + s.getInitiatorName() +
                            " → §a" + s.getDisplayDescription() +
                            " §7(" + s.getRemainingSeconds() + "s restants)"
                        ));
                        var pending = s.getPendingVoters();
                        if (!pending.isEmpty()) {
                            source.sendMessage(net.minecraft.text.Text.literal(
                                "§7En attente de : " + String.join(", ", pending)
                            ));
                        }
                    }
                    return 1;
                })
        );
    }
}
