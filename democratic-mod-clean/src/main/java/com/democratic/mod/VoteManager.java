package com.democratic.mod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.*;

/**
 * Gère tous les votes en cours sur le serveur.
 */
public class VoteManager {

    private static VoteManager instance;
    private final Map<String, VoteSession> activeSessions = new HashMap<>();
    // Clé = nom du joueur initiateur

    public static VoteManager getInstance() {
        if (instance == null) instance = new VoteManager();
        return instance;
    }

    /**
     * Lance un nouveau vote.
     * @return true si le vote a été créé, false si un vote est déjà en cours pour ce joueur
     */
    public boolean startVote(MinecraftServer server, ServerPlayerEntity initiator, String command, String description) {
        String initiatorName = initiator.getName().getString();

        if (activeSessions.containsKey(initiatorName)) {
            initiator.sendMessage(Text.literal("§c[Vote] Tu as déjà un vote en cours !"), false);
            return false;
        }

        List<ServerPlayerEntity> allPlayers = server.getPlayerManager().getPlayerList();
        VoteSession session = new VoteSession(initiator, command, description, allPlayers);

        // Si l'initiateur est seul, exécuter directement
        if (session.getRequiredVoters().isEmpty()) {
            executeCommand(server, initiator, command);
            return true;
        }

        activeSessions.put(initiatorName, session);

        // Notifier tout le monde
        broadcastVoteRequest(server, initiator, description, session);
        return true;
    }

    private void broadcastVoteRequest(MinecraftServer server, ServerPlayerEntity initiator, String description, VoteSession session) {
        String name = initiator.getName().getString();
        long seconds = session.getRemainingSeconds();

        Text message = Text.literal("")
            .append(Text.literal("━━━━━━━━━━━━━━━━━━━━━━━━━\n").formatted(Formatting.GOLD))
            .append(Text.literal("⚖ VOTE REQUIS\n").formatted(Formatting.YELLOW, Formatting.BOLD))
            .append(Text.literal(name).formatted(Formatting.AQUA))
            .append(Text.literal(" veut faire : ").formatted(Formatting.WHITE))
            .append(Text.literal(description + "\n").formatted(Formatting.GREEN))
            .append(Text.literal("Votez avec : ").formatted(Formatting.GRAY))
            .append(Text.literal("/voteyes ✅").formatted(Formatting.GREEN))
            .append(Text.literal("  ou  ").formatted(Formatting.GRAY))
            .append(Text.literal("/voteno ❌\n").formatted(Formatting.RED))
            .append(Text.literal("Temps restant : " + seconds + "s\n").formatted(Formatting.YELLOW))
            .append(Text.literal("━━━━━━━━━━━━━━━━━━━━━━━━━").formatted(Formatting.GOLD));

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            // Tous les joueurs voient le message (y compris l'initiateur)
            if (!player.getName().getString().equals(name)) {
                player.sendMessage(message, false);
            } else {
                player.sendMessage(Text.literal("§e[Vote] Ta demande a été envoyée à tous les joueurs. Ils ont " + seconds + "s pour voter."), false);
            }
        }
    }

    /**
     * Enregistre un vote d'un joueur.
     */
    public void castVote(MinecraftServer server, ServerPlayerEntity voter, boolean approve) {
        String voterName = voter.getName().getString();
        VoteSession targetSession = null;
        String initiatorKey = null;

        // Trouver le(s) vote(s) en attente pour ce joueur
        for (Map.Entry<String, VoteSession> entry : activeSessions.entrySet()) {
            if (entry.getValue().getRequiredVoters().contains(voterName)) {
                // Priorité au premier vote trouvé
                if (!entry.getValue().getVotes().containsKey(voterName)) {
                    targetSession = entry.getValue();
                    initiatorKey = entry.getKey();
                    break;
                }
            }
        }

        if (targetSession == null) {
            voter.sendMessage(Text.literal("§c[Vote] Aucun vote en cours pour toi."), false);
            return;
        }

        boolean success = targetSession.castVote(voterName, approve);
        if (!success) {
            voter.sendMessage(Text.literal("§c[Vote] Tu as déjà voté."), false);
            return;
        }

        String emoji = approve ? "✅" : "❌";
        // Informer tout le monde du vote
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            p.sendMessage(Text.literal("§7[Vote] §f" + voterName + " a voté " + emoji), false);
        }

        // Vérifier le résultat
        checkAndResolve(server, initiatorKey, targetSession);
    }

    private void checkAndResolve(MinecraftServer server, String initiatorKey, VoteSession session) {
        VoteSession.VoteResult result = session.getResult();

        if (result == VoteSession.VoteResult.APPROVED) {
            activeSessions.remove(initiatorKey);
            ServerPlayerEntity initiator = server.getPlayerManager().getPlayer(session.getInitiatorName());

            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                p.sendMessage(Text.literal("§a[Vote] ✅ Vote approuvé ! La commande est exécutée."), false);
            }

            if (initiator != null) {
                executeCommand(server, initiator, session.getCommandToExecute());
            }

        } else if (result == VoteSession.VoteResult.REJECTED) {
            activeSessions.remove(initiatorKey);
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                p.sendMessage(Text.literal("§c[Vote] ❌ Vote refusé ! La commande n'est pas exécutée."), false);
            }
        }
        // Sinon PENDING → on attend
    }

    /**
     * Appelé à chaque tick serveur pour vérifier les timeouts.
     */
    public void tick(MinecraftServer server) {
        List<String> toRemove = new ArrayList<>();

        for (Map.Entry<String, VoteSession> entry : activeSessions.entrySet()) {
            VoteSession session = entry.getValue();
            if (session.isTimedOut()) {
                toRemove.add(entry.getKey());
                for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                    p.sendMessage(Text.literal("§c[Vote] ⌛ Temps écoulé ! Le vote de " + session.getInitiatorName() + " a été annulé."), false);
                }
                Set<String> pending = session.getPendingVoters();
                if (!pending.isEmpty()) {
                    for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                        p.sendMessage(Text.literal("§7[Vote] N'ont pas voté : " + String.join(", ", pending)), false);
                    }
                }
            }
        }

        for (String key : toRemove) {
            activeSessions.remove(key);
        }
    }

    private void executeCommand(MinecraftServer server, ServerPlayerEntity player, String command) {
        // Exécute la commande comme si le joueur la tapait
        server.getCommandManager().executeWithPrefix(player.getCommandSource().withMaxLevel(4), command);
    }

    public boolean hasActiveVote(String playerName) {
        return activeSessions.containsKey(playerName);
    }

    public Map<String, VoteSession> getActiveSessions() {
        return Collections.unmodifiableMap(activeSessions);
    }
}
