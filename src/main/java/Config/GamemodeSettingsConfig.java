package Config;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Stores the global gamemode defaults per world and short lived creative overrides.
 */
public final class GamemodeSettingsConfig {

    private static final String DEFAULTS_PATH = "worldDefaults";
    private static final String OVERRIDES_PATH = "creativeOverrides";
    private static final Duration OVERRIDE_DURATION = Duration.ofHours(7);

    private static File configFile;
    private static FileConfiguration config;
    private static JavaPlugin plugin;

    private GamemodeSettingsConfig() {
    }

    public static void initialize(JavaPlugin instance) {
        plugin = instance;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        configFile = new File(plugin.getDataFolder(), "world_gamemodes.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Konnte world_gamemodes.yml nicht erstellen: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        purgeExpiredOverrides(); // cleanup once during startup
    }

    public static void save() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            if (plugin != null) {
                plugin.getLogger().warning("Konnte world_gamemodes.yml nicht speichern: " + e.getMessage());
            }
        }
    }

    public static Map<String, GameMode> getAllWorldDefaults() {
        if (config == null) {
            return Collections.emptyMap();
        }
        Map<String, GameMode> defaults = new HashMap<>();
        if (config.isConfigurationSection(DEFAULTS_PATH)) {
            for (String worldName : config.getConfigurationSection(DEFAULTS_PATH).getKeys(false)) {
                defaults.put(worldName, parseGameMode(config.getString(DEFAULTS_PATH + "." + worldName)));
            }
        }
        Bukkit.getWorlds().forEach(world -> defaults.putIfAbsent(world.getName(), GameMode.ADVENTURE));
        return defaults;
    }

    public static GameMode getWorldGamemode(String worldName) {
        if (worldName == null || config == null) {
            return GameMode.ADVENTURE;
        }
        String stored = config.getString(DEFAULTS_PATH + "." + worldName);
        if (stored == null) {
            return GameMode.ADVENTURE;
        }
        return parseGameMode(stored);
    }

    public static void setWorldGamemode(String worldName, GameMode mode) {
        if (worldName == null || config == null) {
            return;
        }
        if (mode == null || mode == GameMode.ADVENTURE) {
            config.set(DEFAULTS_PATH + "." + worldName, null);
        } else {
            config.set(DEFAULTS_PATH + "." + worldName, mode.name());
        }
        save();
    }

    private static GameMode parseGameMode(String value) {
        if (value == null) {
            return GameMode.ADVENTURE;
        }
        try {
            return GameMode.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return GameMode.ADVENTURE;
        }
    }

    public static GameMode resolveGamemodeForWorld(String worldName) {
        return getWorldGamemode(worldName);
    }

    public static boolean hasCreativeOverride(UUID playerId) {
        return getCreativeOverrideExpiry(playerId) > System.currentTimeMillis();
    }

    public static long getCreativeOverrideExpiry(UUID playerId) {
        if (playerId == null || config == null) {
            return 0L;
        }
        long expiresAt = config.getLong(overridePath(playerId) + ".expiresAt", 0L);
        if (expiresAt <= System.currentTimeMillis()) {
            if (expiresAt > 0L) {
                clearOverride(playerId);
            }
            return 0L;
        }
        return expiresAt;
    }

    public static long enableCreativeOverride(UUID playerId, String playerName) {
        if (playerId == null || config == null) {
            return 0L;
        }
        long expiresAt = System.currentTimeMillis() + OVERRIDE_DURATION.toMillis();
        String base = overridePath(playerId);
        config.set(base + ".expiresAt", expiresAt);
        if (playerName != null) {
            config.set(base + ".player", playerName);
        }
        save();
        return expiresAt;
    }

    public static void clearOverride(UUID playerId) {
        if (playerId == null || config == null) {
            return;
        }
        config.set(overridePath(playerId), null);
        save();
    }

    public static Map<UUID, Long> getActiveOverrides() {
        if (config == null || !config.isConfigurationSection(OVERRIDES_PATH)) {
            return Collections.emptyMap();
        }
        Map<UUID, Long> result = new HashMap<>();
        for (String key : config.getConfigurationSection(OVERRIDES_PATH).getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long expires = config.getLong(OVERRIDES_PATH + "." + key + ".expiresAt", 0L);
                if (expires > System.currentTimeMillis()) {
                    result.put(uuid, expires);
                } else {
                    config.set(OVERRIDES_PATH + "." + key, null);
                }
            } catch (IllegalArgumentException ignored) {
                config.set(OVERRIDES_PATH + "." + key, null);
            }
        }
        save();
        return result;
    }

    public static void purgeExpiredOverrides() {
        if (config == null || !config.isConfigurationSection(OVERRIDES_PATH)) {
            return;
        }
        boolean changed = false;
        for (String key : config.getConfigurationSection(OVERRIDES_PATH).getKeys(false)) {
            long expires = config.getLong(OVERRIDES_PATH + "." + key + ".expiresAt", 0L);
            if (expires <= System.currentTimeMillis()) {
                config.set(OVERRIDES_PATH + "." + key, null);
                changed = true;
            }
        }
        if (changed) {
            save();
        }
    }

    private static String overridePath(UUID playerId) {
        return OVERRIDES_PATH + "." + playerId;
    }

    public static GameMode resolveGamemodeForPlayer(UUID playerId, String worldName) {
        if (playerId != null && hasCreativeOverride(playerId)) {
            return GameMode.CREATIVE;
        }
        return resolveGamemodeForWorld(worldName);
    }
}
