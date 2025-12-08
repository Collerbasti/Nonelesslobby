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

    // Track last enforced gamemode to avoid redundant checks
    private static final Map<UUID, GameMode> lastEnforcedMode = new HashMap<>();

    private GamemodeEnforcer() {
    }

    public static void start(JavaPlugin plugin) {
        // Increased interval from 40 ticks (2s) to 100 ticks (5s) for better performance
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
        
        // Skip if player has bypass permission
        if (player.hasPermission("NonelessLobby.bypassGamemode")) {
            return;
        }
        
        GameMode target = GamemodeSettingsConfig.resolveGamemodeForPlayer(
                player.getUniqueId(),
                player.getWorld() != null ? player.getWorld().getName() : null
        );
        
        if (target == null) {
            return;
        }
        
        // Only update if different from current mode AND different from last enforced
        GameMode currentMode = player.getGameMode();
        GameMode lastMode = lastEnforcedMode.get(player.getUniqueId());
        
        if (currentMode != target && lastMode != target) {
            player.setGameMode(target);
            lastEnforcedMode.put(player.getUniqueId(), target);
        }
    }
    
    /**
     * Call this when a player changes world to immediately enforce gamemode
     */
    public static void enforceImmediate(Player player) {
        if (player != null && player.isOnline()) {
            // Clear cached mode to force re-check
            lastEnforcedMode.remove(player.getUniqueId());
            enforce(player);
        }
    }
    
    /**
     * Clear cached mode for a player (e.g., on logout)
     */
    public static void clearPlayer(Player player) {
        if (player != null) {
            lastEnforcedMode.remove(player.getUniqueId());
        }
    }
}
