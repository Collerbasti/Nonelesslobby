package Config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import de.noneless.lobby.Main;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    
    private static Location lobbyLocation;
    private static File configFile;
    private static FileConfiguration config;
    private static Main plugin;
    private static String storedWorldName;
    private static double storedX;
    private static double storedY;
    private static double storedZ;
    private static float storedYaw;
    private static float storedPitch;
    private static boolean storedLocationResolved;
    private static boolean lobbyLogged;
    
    public static void initialize(Main instance) {
        plugin = instance;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        configFile = new File(plugin.getDataFolder(), "lobby.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Konnte lobby.yml nicht erstellen: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadStoredLocationValues();
        lobbyLocation = resolveStoredLocation();
        storedLocationResolved = lobbyLocation != null;
        lobbyLogged = false;
        if (lobbyLocation == null) {
            lobbyLocation = plugin.getServer().getWorlds().isEmpty()
                    ? null
                    : plugin.getServer().getWorlds().get(0).getSpawnLocation();
            if (lobbyLocation != null) {
                storedLocationResolved = true;
            }
        }
        logLobbyLocation("Initialisiert");
    }
    
    public static Location getLobbyLocation() {
        attemptResolveStoredLocation();
        if (lobbyLocation == null && !plugin.getServer().getWorlds().isEmpty()) {
            lobbyLocation = plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }
        logLobbyLocation("Abfrage");
        return lobbyLocation;
    }
    
    public static void setLobbyLocation(Location location) {
        lobbyLocation = location;
        if (config == null || location == null) return;
        config.set("lobby.world", location.getWorld().getName());
        config.set("lobby.x", location.getX());
        config.set("lobby.y", location.getY());
        config.set("lobby.z", location.getZ());
        config.set("lobby.yaw", location.getYaw());
        config.set("lobby.pitch", location.getPitch());
        storedWorldName = location.getWorld().getName();
        storedX = location.getX();
        storedY = location.getY();
        storedZ = location.getZ();
        storedYaw = location.getYaw();
        storedPitch = location.getPitch();
        storedLocationResolved = true;
        saveConfig();
        logLobbyLocation("Gespeichert");
    }
    
    private static void saveConfig() {
        if (config == null || configFile == null) return;
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte lobby.yml nicht speichern: " + e.getMessage());
        }
    }
    
    private static void loadStoredLocationValues() {
        if (config == null) {
            storedWorldName = null;
            return;
        }
        storedWorldName = config.getString("lobby.world");
        storedX = config.getDouble("lobby.x", 0);
        storedY = config.getDouble("lobby.y", 0);
        storedZ = config.getDouble("lobby.z", 0);
        storedYaw = (float) config.getDouble("lobby.yaw", 0);
        storedPitch = (float) config.getDouble("lobby.pitch", 0);
    }
    
    private static void attemptResolveStoredLocation() {
        if (storedLocationResolved) {
            return;
        }
        Location resolved = resolveStoredLocation();
        if (resolved != null) {
            lobbyLocation = resolved;
            storedLocationResolved = true;
            logLobbyLocation("Geladen");
        }
    }

    private static Location resolveStoredLocation() {
        if (storedWorldName == null || plugin == null) {
            return null;
        }
        World world = plugin.getServer().getWorld(storedWorldName);
        if (world == null) {
            return null;
        }
        return new Location(world, storedX, storedY, storedZ, storedYaw, storedPitch);
    }

    public static void handleWorldLoad(World world) {
        if (world == null || storedWorldName == null) {
            return;
        }
        if (storedLocationResolved) {
            return;
        }
        if (world.getName().equalsIgnoreCase(storedWorldName)) {
            attemptResolveStoredLocation();
        }
    }

    private static void logLobbyLocation(String context) {
        if (plugin == null || lobbyLocation == null) {
            return;
        }
        if (lobbyLogged && !"Gespeichert".equals(context)) {
            return;
        }
        plugin.getLogger().info(String.format(
                "Lobby Position [%s]: %s (%.2f / %.2f / %.2f) yaw=%.2f pitch=%.2f",
                context,
                lobbyLocation.getWorld() != null ? lobbyLocation.getWorld().getName() : "unbekannte Welt",
                lobbyLocation.getX(),
                lobbyLocation.getY(),
                lobbyLocation.getZ(),
                lobbyLocation.getYaw(),
                lobbyLocation.getPitch()
        ));
        lobbyLogged = true;
    }
}
