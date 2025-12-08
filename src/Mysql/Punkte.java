package Mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import Main.MySQL;

public class Punkte {
    // Cache für bessere Performance
    private static Map<UUID, Integer> pointsCache = new HashMap<>();
    
    /**
     * Holt die Punkte eines Spielers aus der Datenbank
     */
    public static int getPoints(UUID uuid) {
        // Prüfe zuerst den Cache
        if (pointsCache.containsKey(uuid)) {
            return pointsCache.get(uuid);
        }
        
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(
                "SELECT points FROM player_points WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            
            int points = 0;
            if (rs.next()) {
                points = rs.getInt("points");
            }
            
            // In Cache speichern
            pointsCache.put(uuid, points);
            
            rs.close();
            ps.close();
            return points;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Setzt die Punkte eines Spielers
     */
    public static void setPoints(UUID uuid, int points) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(
                "INSERT INTO player_points (uuid, points) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE points = ?");
            ps.setString(1, uuid.toString());
            ps.setInt(2, points);
            ps.setInt(3, points);
            ps.executeUpdate();
            ps.close();
            
            // Cache aktualisieren
            pointsCache.put(uuid, points);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Fügt Punkte hinzu
     */
    public static void addPoints(UUID uuid, int amount) {
        int currentPoints = getPoints(uuid);
        setPoints(uuid, currentPoints + amount);
    }
    
    /**
     * Entfernt Punkte
     */
    public static void removePoints(UUID uuid, int amount) {
        int currentPoints = getPoints(uuid);
        setPoints(uuid, Math.max(0, currentPoints - amount));
    }
    
    /**
     * Aktualisiert Punkte mit Nachricht an den Spieler
     */
    public static void updatePoints(UUID uuid, int amount, String reason, boolean add, Player player) {
        int currentPoints = getPoints(uuid);
        
        if (add) {
            setPoints(uuid, currentPoints + amount);
            if (player != null) {
                player.sendMessage("§a+ " + amount + " Punkte für " + reason);
            }
        } else {
            setPoints(uuid, Math.max(0, currentPoints - amount));
            if (player != null) {
                player.sendMessage("§c- " + amount + " Punkte für " + reason);
            }
        }
    }
    
    /**
     * Initialisiert die Datenbanktabelle
     */
    public static void initializeDatabase() {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS player_points (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "points INT DEFAULT 0, " +
                "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")");
            ps.executeUpdate();
            ps.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Leert den Cache (nützlich beim Plugin-Reload)
     */
    public static void clearCache() {
        pointsCache.clear();
    }
    
    /**
     * Lädt die Punkte eines Spielers in den Cache
     */
    public static void loadPlayerToCache(UUID uuid) {
        getPoints(uuid); // Lädt automatisch in den Cache
    }
}