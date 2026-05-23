package com.democratic.mod;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Enregistre les événements nécessaires pour intercepter certaines actions.
 * L'interception principale des commandes se fait via le Mixin.
 */
public class CommandInterceptor {

    // Liste des commandes qui nécessitent un vote
    public static final String[] PROTECTED_COMMANDS = {
        "gamemode", "gm",
        "op", "deop",
        "give",
        "weather",
        "time set",
        "difficulty",
        "ban", "ban-ip", "pardon",
        "kick",
        "stop",
        "whitelist",
        "fill",
        "clone",
        "setblock",
        "summon",
        "tp", "teleport",
        "effect",
        "enchant",
        "xp", "experience",
        "kill",
        "title",
        "clear"
    };

    public static void register() {
        // L'interception principale est gérée par le Mixin (ServerPlayerEntityMixin)
        DemocraticMod.LOGGER.info("Intercepteur de commandes actif. Commandes protégées : " + PROTECTED_COMMANDS.length);
    }

    /**
     * Vérifie si une commande doit passer par un vote.
     */
    public static boolean isProtectedCommand(String command) {
        String lower = command.toLowerCase().trim();
        // Supprimer le slash initial si présent
        if (lower.startsWith("/")) lower = lower.substring(1);

        for (String protected_cmd : PROTECTED_COMMANDS) {
            if (lower.equals(protected_cmd) || lower.startsWith(protected_cmd + " ")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne une description lisible de la commande pour affichage dans le vote.
     */
    public static String getCommandDescription(String command) {
        String lower = command.toLowerCase().trim();
        if (lower.startsWith("/")) lower = lower.substring(1);

        if (lower.startsWith("gamemode") || lower.startsWith("gm")) return "Changer le mode de jeu (" + command + ")";
        if (lower.startsWith("give")) return "Donner des items (" + command + ")";
        if (lower.startsWith("op")) return "Donner les droits OP (" + command + ")";
        if (lower.startsWith("deop")) return "Retirer les droits OP (" + command + ")";
        if (lower.startsWith("weather")) return "Changer la météo (" + command + ")";
        if (lower.startsWith("time")) return "Changer l'heure (" + command + ")";
        if (lower.startsWith("difficulty")) return "Changer la difficulté (" + command + ")";
        if (lower.startsWith("stop")) return "Arrêter le serveur !";
        if (lower.startsWith("tp") || lower.startsWith("teleport")) return "Téléportation (" + command + ")";
        if (lower.startsWith("kill")) return "Tuer un joueur (" + command + ")";
        if (lower.startsWith("ban")) return "Bannir un joueur (" + command + ")";
        if (lower.startsWith("kick")) return "Expulser un joueur (" + command + ")";
        if (lower.startsWith("fill")) return "Remplir une zone de blocs (" + command + ")";
        if (lower.startsWith("effect")) return "Appliquer un effet (" + command + ")";
        if (lower.startsWith("enchant")) return "Enchanter un item (" + command + ")";
        if (lower.startsWith("clear")) return "Vider l'inventaire (" + command + ")";
        return "Commande protégée : " + command;
    }
}
