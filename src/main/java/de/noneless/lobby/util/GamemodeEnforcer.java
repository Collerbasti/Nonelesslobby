package de.noneless.lobby.util;

import Config.GamemodeSettingsConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GamemodeEnforcer {

    // Track last enforced gamemode to avoid redundant messages
    private static final Map<UUID, GameMode> lastEnforcedMode = new HashMap<>();

    private GamemodeEnforcer() {
    }

    public static void start(JavaPlugin plugin) {
        // Periodic check every 5 seconds
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                enforce(player);
            }
        }, 20L, 100L); // start after 1s, repeat every 5s
    }

    public static void enforce(Player player) {
        if (player == null || !player.isOnline() || player.isDead()) {
            return;
        }
        
        // Gamemode wird für ALLE Spieler erzwungen - kein Bypass möglich
        
        GameMode target = GamemodeSettingsConfig.resolveGamemodeForPlayer(
                player.getUniqueId(),
                player.getWorld() != null ? player.getWorld().getName() : null
        );
        
        if (target == null) {
            return;
        }
        
        GameMode currentMode = player.getGameMode();
        
        // FIX: Always enforce if current mode doesn't match target
        if (currentMode != target) {
            player.setGameMode(target);
            lastEnforcedMode.put(player.getUniqueId(), target);
        }
    }
    
    /**
     * Call this when a player changes world or teleports to immediately enforce gamemode.
     * Uses a slight delay to ensure the player is fully in the new location.
     */
    public static void enforceImmediate(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        
        // Clear cached mode to force re-check
        lastEnforcedMode.remove(player.getUniqueId());
        
        // Enforce immediately
        enforce(player);
        
        // Also schedule a delayed check in case of timing issues
        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("NonelessLobby"),
            () -> {
                if (player.isOnline()) {
                    enforce(player);
                }
            },
            5L // 0.25 seconds delay
        );
    }
    
    /**
     * Clear cached mode for a player (e.g., on logout)
     */
    public static void clearPlayer(Player player) {
        if (player != null) {
            lastEnforcedMode.remove(player.getUniqueId());
        }
    }
    
    /**
     * Gets the target gamemode for a player (for external checks)
     */
    public static GameMode getTargetGamemode(Player player) {
        if (player == null) {
            return GameMode.ADVENTURE;
        }
        // Gamemode wird für ALLE erzwungen
        GameMode target = GamemodeSettingsConfig.resolveGamemodeForPlayer(
                player.getUniqueId(),
                player.getWorld() != null ? player.getWorld().getName() : null
        );
        return target != null ? target : GameMode.ADVENTURE;
    }
}
