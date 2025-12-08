package de.noneless.lobby.util;

import Config.GamemodeSettingsConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class GamemodeEnforcer {

    private GamemodeEnforcer() {
    }

    public static void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                enforce(player);
            }
        }, 20L, 40L); // start after 1s, repeat every 2s
    }

    public static void enforce(Player player) {
        if (player == null || !player.isOnline() || player.isDead()) {
            return;
        }
        GameMode target = GamemodeSettingsConfig.resolveGamemodeForPlayer(
                player.getUniqueId(),
                player.getWorld() != null ? player.getWorld().getName() : null
        );
        if (target == null) {
            return;
        }
        if (player.getGameMode() != target) {
            player.setGameMode(target);
        }
    }
}
