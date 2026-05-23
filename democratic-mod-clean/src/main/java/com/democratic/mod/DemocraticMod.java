package com.democratic.mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemocraticMod implements ModInitializer {

    public static final String MOD_ID = "democratic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Democratic Mod chargé ! Les commandes nécessitent un vote.");

        // Enregistrer les commandes du mod
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            VoteCommand.register(dispatcher);
        });

        // Tick serveur pour gérer les timeouts de votes
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            VoteManager.getInstance().tick(server);
        });

        // Intercepter les commandes dangereuses
        CommandInterceptor.register();
    }
}
