package de.noneless.lobby.listeners;

import Config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class LobbyWorldListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        ConfigManager.handleWorldLoad(event.getWorld());
    }
}
