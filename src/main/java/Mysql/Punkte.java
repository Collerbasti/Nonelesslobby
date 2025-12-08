package Mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import Mysql.MySQL;

public class Punkte {
    // Thread-safe cache for better performance
    private static final Map<UUID, Integer> pointsCache = new ConcurrentHashMap<>();
    
    // Track dirty entries that need to be saved
    private static final Map<UUID, Integer> dirtyCache = new ConcurrentHashMap<>();
    
    /**
     * Gets a player's points from cache or database
     */
    public static int getPoints(UUID uuid) {
        // Check cache first
        if (pointsCache.containsKey(uuid)) {
            return pointsCache.get(uuid);
        }
        
        // Load from database
        return loadPointsFromDatabase(uuid);
    }
    
    /**
     * Loads points from database synchronously (for initial load)
     */
    private static int loadPointsFromDatabase(UUID uuid) {
        try (Connection conn = MySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT points FROM player_points WHERE uuid = ?")) {
            
            ps.setString(1, uuid.toString());
            
            try (ResultSet rs = ps.executeQuery()) {
                int points = 0;
                if (rs.next()) {
                    points = rs.getInt("points");
                }
                
                // Cache the result
                pointsCache.put(uuid, points);
                return points;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[Lobby-MySQL] Error getting points: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Sets a player's points - updates cache and marks for async save
     */
    public static void setPoints(UUID uuid, int points) {
        // Update cache immediately
        pointsCache.put(uuid, points);
        dirtyCache.put(uuid, points);
        
        // Save asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(
            Bukkit.getPluginManager().getPlugin("NonelessLobby"), 
            () -> savePointsToDatabase(uuid, points)
        );
    }
    
    /**
     * Saves points to database (called async)
     */
    private static void savePointsToDatabase(UUID uuid, int points) {
        try (Connection conn = MySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO player_points (uuid, points) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE points = ?")) {
            
            ps.setString(1, uuid.toString());
            ps.setInt(2, points);
            ps.setInt(3, points);
            ps.executeUpdate();
            
            // Remove from dirty cache after successful save
            dirtyCache.remove(uuid);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[Lobby-MySQL] Error setting points: " + e.getMessage());
        }
    }
    
    /**
     * Adds points to a player
     */
    public static void addPoints(UUID uuid, int amount) {
        int currentPoints = getPoints(uuid);
        setPoints(uuid, currentPoints + amount);
    }
    
    /**
     * Removes points from a player
     */
    public static void removePoints(UUID uuid, int amount) {
        int currentPoints = getPoints(uuid);
        setPoints(uuid, Math.max(0, currentPoints - amount));
    }
    
    /**
     * Updates points with a message to the player
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
     * Initializes the database table
     */
    public static void initializeDatabase() {
        Bukkit.getScheduler().runTaskAsynchronously(
            Bukkit.getPluginManager().getPlugin("NonelessLobby"),
            () -> {
                try (Connection conn = MySQL.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS player_points (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "points INT DEFAULT 0, " +
                        "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                        "INDEX idx_points (points)" + // Add index for faster leaderboard queries
                        ")")) {
                    ps.executeUpdate();
                    Bukkit.getLogger().info("[Lobby-MySQL] player_points table initialized");
                } catch (SQLException e) {
                    Bukkit.getLogger().severe("[Lobby-MySQL] Error initializing table: " + e.getMessage());
                }
            }
        );
    }
    
    /**
     * Clears the cache (useful for plugin reload)
     */
    public static void clearCache() {
        pointsCache.clear();
        dirtyCache.clear();
    }
    
    /**
     * Loads a player's points into cache
     */
    public static void loadPlayerToCache(UUID uuid) {
        getPoints(uuid); // This automatically loads into cache
    }

    /**
     * Saves all dirty cached points to database using batch operations.
     * Called when plugin disables.
     */
    public static void saveAllPoints() {
        if (dirtyCache.isEmpty()) {
            return;
        }
        
        try (Connection conn = MySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO player_points (uuid, points) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE points = ?")) {

            // Use batch operations for better performance
            for (Map.Entry<UUID, Integer> entry : dirtyCache.entrySet()) {
                ps.setString(1, entry.getKey().toString());
                ps.setInt(2, entry.getValue());
                ps.setInt(3, entry.getValue());
                ps.addBatch();
            }

            ps.executeBatch();
            dirtyCache.clear();
            Bukkit.getLogger().info("[Lobby-MySQL] Alle Punkte gespeichert");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[Lobby-MySQL] Error saving all points: " + e.getMessage());
        }
    }
}