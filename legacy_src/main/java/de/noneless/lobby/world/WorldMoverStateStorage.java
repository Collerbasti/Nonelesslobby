package de.noneless.lobby.world;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Persists the current world mover job to disk so it can resume after restarts.
 */
public class WorldMoverStateStorage {

    private final JavaPlugin plugin;
    private final File file;

    public WorldMoverStateStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "worldmover_state.yml");
    }

    public Optional<WorldMoveState> load() {
        if (!file.exists()) {
            return Optional.empty();
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (!configuration.getBoolean("active", false)) {
            return Optional.empty();
        }

        String worldName = configuration.getString("world");
        int offset = configuration.getInt("offset");
        int minChunkX = configuration.getInt("chunks.minX");
        int maxChunkX = configuration.getInt("chunks.maxX");
        int minChunkZ = configuration.getInt("chunks.minZ");
        int maxChunkZ = configuration.getInt("chunks.maxZ");
        int nextChunkX = configuration.getInt("chunks.nextX", minChunkX);
        int nextChunkZ = configuration.getInt("chunks.nextZ", minChunkZ);
        long processed = configuration.getLong("progress.processedChunks");
        long total = configuration.getLong("progress.totalChunks");
        long startedAt = configuration.getLong("progress.startedAt");
        String initiatorName = configuration.getString("initiator.name");
        String uuidRaw = configuration.getString("initiator.uuid");
        UUID initiatorUuid = null;
        if (uuidRaw != null && !uuidRaw.isEmpty()) {
            try {
                initiatorUuid = UUID.fromString(uuidRaw);
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Ung\u00fcltige Initiator-UUID in worldmover_state.yml: " + uuidRaw);
            }
        }
        boolean initiatedByPlayer = configuration.getBoolean("initiator.player", false);

        return Optional.of(new WorldMoveState(
                worldName,
                offset,
                minChunkX,
                maxChunkX,
                minChunkZ,
                maxChunkZ,
                nextChunkX,
                nextChunkZ,
                processed,
                total,
                startedAt,
                initiatorName,
                initiatorUuid,
                initiatedByPlayer
        ));
    }

    public void save(WorldMoveState state) {
        if (state == null) {
            return;
        }
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            plugin.getLogger().severe("Konnte Verzeichnis f\u00fcr worldmover_state.yml nicht erstellen.");
            return;
        }

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("active", true);
        configuration.set("world", state.worldName());
        configuration.set("offset", state.offset());
        configuration.set("chunks.minX", state.minChunkX());
        configuration.set("chunks.maxX", state.maxChunkX());
        configuration.set("chunks.minZ", state.minChunkZ());
        configuration.set("chunks.maxZ", state.maxChunkZ());
        configuration.set("chunks.nextX", state.nextChunkX());
        configuration.set("chunks.nextZ", state.nextChunkZ());
        configuration.set("progress.processedChunks", state.processedChunks());
        configuration.set("progress.totalChunks", state.totalChunks());
        configuration.set("progress.startedAt", state.startedAtMillis());
        configuration.set("initiator.name", state.initiatorName());
        configuration.set("initiator.uuid", state.initiatorUuid() != null ? state.initiatorUuid().toString() : null);
        configuration.set("initiator.player", state.initiatedByPlayer());

        try {
            configuration.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Konnte worldmover_state.yml nicht speichern.", ex);
        }
    }

    public void clear() {
        if (file.exists() && !file.delete()) {
            plugin.getLogger().warning("Konnte worldmover_state.yml nicht l\u00f6schen.");
        }
    }
}
