package de.noneless.lobby.api;

import java.util.UUID;
import org.bukkit.entity.Player;
import Mysql.Punkte;

/**
 * API-Klasse für das Punkte-System.
 * Diese Klasse ist öffentlich und wird vom MiniCardGame via Reflection aufgerufen.
 */
public class PunkteAPI {

    public static int getPoints(UUID uuid) {
        return Punkte.getPoints(uuid);
    }

    public static void addPoints(UUID uuid, int amount) {
        Punkte.addPoints(uuid, amount);
    }

    public static void removePoints(UUID uuid, int amount) {
        Punkte.removePoints(uuid, amount);
    }

    public static void setPoints(UUID uuid, int amount) {
        Punkte.setPoints(uuid, amount);
    }

    public static void updatePoints(UUID uuid, int amount, String reason, boolean add, Player player) {
        Punkte.updatePoints(uuid, amount, reason, add, player);
    }

    public static void loadPlayerToCache(UUID uuid) {
        Punkte.loadPlayerToCache(uuid);
    }

    public static void clearCache() {
        Punkte.clearCache();
    }
}



