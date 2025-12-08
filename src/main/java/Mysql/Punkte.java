package Mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Einheitliche Punkte-Verwaltung für Lobby und MiniCardGame (via API).
 * Speichert in MySQL-Tabelle player_points.
 */
public class Punkte {
    private static final Map<UUID, Integer> pointsCache = new HashMap<>();

    public static void initializeDatabase() {
        // Sicherstellen, dass DB-Verbindung steht und Tabelle existiert
        MySQL.connect();
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS player_points (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "points INT DEFAULT 0, " +
                "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")"
            );
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getPoints(UUID uuid) {
        Integer cached = pointsCache.get(uuid);
        if (cached != null) {
            return cached;
        }
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(
                "SELECT points FROM player_points WHERE uuid = ?"
            );
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int points = 0;
            if (rs.next()) {
                points = rs.getInt("points");
            }
            rs.close();
            ps.close();
            pointsCache.put(uuid, points);
            return points;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void setPoints(UUID uuid, int points) {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(
                "INSERT INTO player_points (uuid, points) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE points = VALUES(points)"
            );
            ps.setString(1, uuid.toString());
            ps.setInt(2, points);
            ps.executeUpdate();
            ps.close();
            pointsCache.put(uuid, points);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPoints(UUID uuid, int amount) {
        setPoints(uuid, Math.max(0, getPoints(uuid) + amount));
    }

    public static void removePoints(UUID uuid, int amount) {
        setPoints(uuid, Math.max(0, getPoints(uuid) - amount));
    }

    public static void updatePoints(UUID uuid, int amount, String reason, boolean add, Player player) {
        if (add) {
            addPoints(uuid, amount);
            if (player != null) player.sendMessage("§a+ " + amount + " Punkte für " + reason);
        } else {
            removePoints(uuid, amount);
            if (player != null) player.sendMessage("§c- " + amount + " Punkte für " + reason);
        }
    }

    public static void saveAllPoints() {
        // NOP – Änderungen werden direkt persistiert
    }

    public static void clearCache() {
        pointsCache.clear();
    }

    public static void loadPlayerToCache(UUID uuid) {
        getPoints(uuid);
    }
}