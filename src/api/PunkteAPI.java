package de.noneless.lobby.api;

import java.util.UUID;
import org.bukkit.entity.Player;
import Mysql.Punkte;

/**
 * API-Klasse für das Punkte-System
 * Ermöglicht anderen Plugins den Zugriff auf das Punkte-System
 */
public class PunkteAPI {
    
    /**
     * Holt die Punkte eines Spielers
     * @param uuid Die UUID des Spielers
     * @return Die Anzahl der Punkte
     */
    public static int getPoints(UUID uuid) {
        return Punkte.getPoints(uuid);
    }
    
    /**
     * Fügt Punkte zu einem Spieler hinzu
     * @param uuid Die UUID des Spielers
     * @param amount Die Anzahl der hinzuzufügenden Punkte
     */
    public static void addPoints(UUID uuid, int amount) {
        Punkte.addPoints(uuid, amount);
    }
    
    /**
     * Entfernt Punkte von einem Spieler
     * @param uuid Die UUID des Spielers
     * @param amount Die Anzahl der zu entfernenden Punkte
     */
    public static void removePoints(UUID uuid, int amount) {
        Punkte.removePoints(uuid, amount);
    }
    
    /**
     * Setzt die Punkte eines Spielers auf einen bestimmten Wert
     * @param uuid Die UUID des Spielers
     * @param amount Die neue Anzahl der Punkte
     */
    public static void setPoints(UUID uuid, int amount) {
        Punkte.setPoints(uuid, amount);
    }
    
    /**
     * Aktualisiert Punkte mit Nachricht an den Spieler
     * @param uuid Die UUID des Spielers
     * @param amount Die Anzahl der Punkte
     * @param reason Der Grund für die Punkteänderung
     * @param add true = Punkte hinzufügen, false = Punkte entfernen
     * @param player Der Spieler (kann null sein)
     */
    public static void updatePoints(UUID uuid, int amount, String reason, boolean add, Player player) {
        Punkte.updatePoints(uuid, amount, reason, add, player);
    }
    
    /**
     * Lädt die Punkte eines Spielers in den Cache
     * @param uuid Die UUID des Spielers
     */
    public static void loadPlayerToCache(UUID uuid) {
        Punkte.loadPlayerToCache(uuid);
    }
    
    /**
     * Leert den Punkte-Cache
     */
    public static void clearCache() {
        Punkte.clearCache();
    }
} 