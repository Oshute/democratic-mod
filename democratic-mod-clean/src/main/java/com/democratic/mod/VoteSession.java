package com.democratic.mod;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.*;

/**
 * Représente un vote en cours sur le serveur.
 */
public class VoteSession {

    public enum VoteResult {
        PENDING,   // En attente
        APPROVED,  // Approuvé
        REJECTED   // Rejeté
    }

    private final String initiatorName;
    private final String commandToExecute;
    private final String displayDescription;
    private final Set<String> requiredVoters;   // Tous les joueurs sauf l'initiateur
    private final Map<String, Boolean> votes;   // true = oui, false = non
    private final long startTimeMillis;
    private static final long TIMEOUT_MILLIS = 30_000; // 30 secondes

    public VoteSession(ServerPlayerEntity initiator, String command, String description, List<ServerPlayerEntity> allPlayers) {
        this.initiatorName = initiator.getName().getString();
        this.commandToExecute = command;
        this.displayDescription = description;
        this.requiredVoters = new HashSet<>();
        this.votes = new HashMap<>();
        this.startTimeMillis = System.currentTimeMillis();

        // Tous les joueurs connectés (sauf l'initiateur) doivent voter
        for (ServerPlayerEntity player : allPlayers) {
            String name = player.getName().getString();
            if (!name.equals(this.initiatorName)) {
                requiredVoters.add(name);
            }
        }
    }

    public boolean castVote(String playerName, boolean approve) {
        if (!requiredVoters.contains(playerName)) return false;
        if (votes.containsKey(playerName)) return false; // déjà voté
        votes.put(playerName, approve);
        return true;
    }

    public VoteResult getResult() {
        // Si quelqu'un a voté non → rejeté immédiatement
        for (boolean v : votes.values()) {
            if (!v) return VoteResult.REJECTED;
        }

        // Si tout le monde a voté oui → approuvé
        if (votes.size() >= requiredVoters.size() && !requiredVoters.isEmpty()) {
            return VoteResult.APPROVED;
        }

        // Cas spécial : initiateur seul sur le serveur
        if (requiredVoters.isEmpty()) {
            return VoteResult.APPROVED;
        }

        // Timeout dépassé → rejeté
        if (System.currentTimeMillis() - startTimeMillis > TIMEOUT_MILLIS) {
            return VoteResult.REJECTED;
        }

        return VoteResult.PENDING;
    }

    public boolean isTimedOut() {
        return System.currentTimeMillis() - startTimeMillis > TIMEOUT_MILLIS;
    }

    public long getRemainingSeconds() {
        long elapsed = System.currentTimeMillis() - startTimeMillis;
        long remaining = (TIMEOUT_MILLIS - elapsed) / 1000;
        return Math.max(0, remaining);
    }

    public Set<String> getPendingVoters() {
        Set<String> pending = new HashSet<>(requiredVoters);
        pending.removeAll(votes.keySet());
        return pending;
    }

    public String getInitiatorName() { return initiatorName; }
    public String getCommandToExecute() { return commandToExecute; }
    public String getDisplayDescription() { return displayDescription; }
    public Set<String> getRequiredVoters() { return requiredVoters; }
    public Map<String, Boolean> getVotes() { return votes; }
}
