package de.noneless.lobby.listeners;

import de.noneless.lobby.Main;
import de.noneless.lobby.world.WorldMoverService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Prevents players (and Multiverse teleports) from entering a world that is currently being shifted.
 */
public class WorldMoveProtectionListener implements Listener {

    private final WorldMoverService worldMoverService;

    public WorldMoveProtectionListener(WorldMoverService worldMoverService) {
        this.worldMoverService = worldMoverService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) {
            return;
        }
        World targetWorld = event.getTo().getWorld();
        if (!worldMoverService.isWorldRestricted(targetWorld)) {
            return;
        }
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.RED + "Diese Welt wird aktuell verschoben. Du wirst in die Lobby gesendet.");
        event.setCancelled(true);
        sendPlayerToLobby(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (worldMoverService.isWorldRestricted(player.getWorld())) {
            sendPlayerToLobby(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (worldMoverService.isWorldRestricted(player.getWorld())) {
            sendPlayerToLobby(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        Location respawnLocation = event.getRespawnLocation();
        if (respawnLocation == null) {
            return;
        }
        if (!worldMoverService.isWorldRestricted(respawnLocation.getWorld())) {
            return;
        }
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.RED + "Respawn in dieser Welt ist aktuell blockiert. Du wirst zur Lobby gesendet.");
        event.setRespawnLocation(resolveLobbyLocation(player));
        sendPlayerToLobby(player);
    }

    private void sendPlayerToLobby(Player player) {
        player.performCommand("lobby");
        Location lobby = resolveLobbyLocation(player);
        if (lobby != null) {
            player.teleport(lobby);
        }
    }

    private Location resolveLobbyLocation(Player player) {
        Location lobby = Main.getInstance() != null ? Main.getInstance().getLobbyLocation() : null;
        if (lobby != null) {
            return lobby.clone();
        }
        World fallbackWorld = null;
        if (player != null && player.getServer() != null && !player.getServer().getWorlds().isEmpty()) {
            fallbackWorld = player.getServer().getWorlds().get(0);
        } else if (!Bukkit.getWorlds().isEmpty()) {
            fallbackWorld = Bukkit.getWorlds().get(0);
        }
        return fallbackWorld != null ? fallbackWorld.getSpawnLocation() : null;
    }
}
