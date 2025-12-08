package de.noneless.lobby.world;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.logging.Level;

final class WorldMoveJob {

    private final JavaPlugin plugin;
    private final CommandSender initiator;
    private final String initiatorName;
    private final UUID initiatorUuid;
    private final boolean initiatedByPlayer;
    private final World world;
    private final WorldMoverStateStorage storage;
    private final boolean resumed;
    private final int offset;
    private final int minChunkX;
    private final int maxChunkX;
    private final int minChunkZ;
    private final int maxChunkZ;
    private final long totalChunks;
    private final Consumer<WorldMoveJob> completionHook;
    private final AtomicLong processedChunks = new AtomicLong();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean completionNotified = new AtomicBoolean(false);
    private final AtomicBoolean cancelRequested = new AtomicBoolean(false);
    private final Object traversalLock = new Object();
    private final Instant startedAt;
    private volatile Instant lastProgressMessage;
    private volatile String cancelReason = "Abgebrochen";
    private int nextChunkX;
    private int nextChunkZ;
    private boolean traversalFinished;
    private int playerEvictionTaskId = -1;

    WorldMoveJob(JavaPlugin plugin,
                 CommandSender initiator,
                 World world,
                 int offset,
                 WorldMoverStateStorage storage,
                 Consumer<WorldMoveJob> completionHook,
                 WorldMoveState resumeState) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.initiator = Objects.requireNonNull(initiator, "initiator");
        this.world = Objects.requireNonNull(world, "world");
        this.offset = offset;
        this.storage = Objects.requireNonNull(storage, "storage");
        this.completionHook = Objects.requireNonNull(completionHook, "completionHook");
        this.resumed = resumeState != null;

        if (resumeState != null) {
            this.initiatorName = resumeState.initiatorName();
            this.initiatedByPlayer = resumeState.initiatedByPlayer();
            this.initiatorUuid = resumeState.initiatorUuid();
        } else {
            this.initiatorName = initiator.getName();
            this.initiatedByPlayer = initiator instanceof Player;
            this.initiatorUuid = initiatedByPlayer ? ((Player) initiator).getUniqueId() : null;
        }

        BorderWindow borderWindow;
        if (resumeState != null) {
            borderWindow = new BorderWindow(
                    resumeState.minChunkX(),
                    resumeState.maxChunkX(),
                    resumeState.minChunkZ(),
                    resumeState.maxChunkZ()
            );
        } else {
            borderWindow = resolveBorder(world);
        }

        this.minChunkX = borderWindow.minChunkX;
        this.maxChunkX = borderWindow.maxChunkX;
        this.minChunkZ = borderWindow.minChunkZ;
        this.maxChunkZ = borderWindow.maxChunkZ;

        if (resumeState != null && resumeState.totalChunks() > 0) {
            this.totalChunks = resumeState.totalChunks();
        } else {
            this.totalChunks = computeTotalChunks();
        }
        if (this.totalChunks <= 0) {
            throw new IllegalStateException("Keine Chunks innerhalb der Arbeitsgrenzen gefunden.");
        }

        this.startedAt = resumeState != null
                ? Instant.ofEpochMilli(resumeState.startedAtMillis())
                : Instant.now();
        this.lastProgressMessage = this.startedAt;

        if (resumeState != null) {
            this.nextChunkX = clamp(resumeState.nextChunkX(), minChunkX, maxChunkX);
            this.nextChunkZ = clamp(resumeState.nextChunkZ(), minChunkZ, maxChunkZ);
            this.processedChunks.set(Math.max(0L, Math.min(resumeState.processedChunks(), totalChunks)));
            this.traversalFinished = processedChunks.get() >= totalChunks;
        } else {
            this.nextChunkX = minChunkX;
            this.nextChunkZ = minChunkZ;
            this.traversalFinished = false;
        }
    }

    void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        if (resumed) {
            sendToInitiator(ChatColor.YELLOW + "WorldMover wird nach einem Neustart fortgesetzt. Offene Chunks: "
                    + (totalChunks - processedChunks.get()));
        } else {
            sendToInitiator(ChatColor.GREEN + "Starte WorldMover f\u00fcr Welt '" + world.getName()
                    + "'. Zu verarbeitende Chunks: " + totalChunks);
        }
        plugin.getLogger().info("WorldMover " + (resumed ? "Resumee" : "Start") + " f\u00fcr Welt " + world.getName()
                + " mit Offset " + offset + " (" + processedChunks.get() + "/" + totalChunks + " Chunks erledigt).");

        startPlayerEvictionTask();
        persistProgress();
        scheduleNextChunk();
    }

    void cancel(String reason) {
        cancelReason = reason == null ? "Abgebrochen" : reason;
        if (!cancelRequested.compareAndSet(false, true)) {
            return;
        }
        sendToInitiator(ChatColor.RED + "WorldMover-Abbruch angefordert: " + cancelReason);
    }

    boolean isRunning() {
        return running.get() && !completionNotified.get();
    }

    String describeProgress() {
        long done = processedChunks.get();
        double percent = (double) done / (double) totalChunks * 100.0D;
        Duration elapsed = Duration.between(startedAt, Instant.now());
        String etaText = buildEta(done, elapsed);
        return ChatColor.AQUA + "WorldMover " + (isRunning() ? "l\u00e4uft" : "steht") + ChatColor.GRAY + " | "
                + done + "/" + totalChunks + " Chunks (" + formatPercent(percent) + "%)" + ChatColor.GRAY
                + " | Laufzeit: " + formatDuration(elapsed) + " | ETA: " + etaText;
    }

    private void scheduleNextChunk() {
        if (!running.get()) {
            return;
        }
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskAsynchronously(plugin, this::processNextChunk);
    }

    private void processNextChunk() {
        if (!running.get()) {
            return;
        }
        if (cancelRequested.get()) {
            finish(true);
            return;
        }

        ChunkCoordinate coordinate = nextChunkCoordinate();
        if (coordinate == null) {
            finish(false);
            return;
        }

        CompletableFuture<Chunk> future = world.getChunkAtAsync(coordinate.x(), coordinate.z(), true);
        future.whenComplete((chunk, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.SEVERE, "Konnte Chunk " + coordinate + " nicht laden.", throwable);
                onChunkProcessed();
                return;
            }

            if (chunk == null) {
                onChunkProcessed();
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> handleChunk(chunk));
        });
    }

    private void handleChunk(Chunk chunk) {
        try {
            shiftChunk(chunk);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Fehler beim Verschieben von Chunk " + chunk.getX() + "," + chunk.getZ(), ex);
        } finally {
            onChunkProcessed();
        }
    }

    private void onChunkProcessed() {
        long done = processedChunks.incrementAndGet();
        maybeReportProgress(done);
        persistProgress();
        if (cancelRequested.get()) {
            finish(true);
        } else {
            scheduleNextChunk();
        }
    }

    private void shiftChunk(Chunk chunk) {
        int chunkBlockX = chunk.getX() << 4;
        int chunkBlockZ = chunk.getZ() << 4;
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        if (offset > 0) {
            for (int y = maxY - 1; y >= minY; y--) {
                int targetY = y + offset;
                if (targetY >= maxY) {
                    continue;
                }

                for (int localX = 0; localX < 16; localX++) {
                    int worldX = chunkBlockX + localX;
                    for (int localZ = 0; localZ < 16; localZ++) {
                        int worldZ = chunkBlockZ + localZ;
                        moveBlock(worldX, y, worldZ, targetY);
                    }
                }
            }
        } else {
            for (int y = minY; y < maxY; y++) {
                int targetY = y + offset;
                if (targetY < minY) {
                    continue;
                }

                for (int localX = 0; localX < 16; localX++) {
                    int worldX = chunkBlockX + localX;
                    for (int localZ = 0; localZ < 16; localZ++) {
                        int worldZ = chunkBlockZ + localZ;
                        moveBlock(worldX, y, worldZ, targetY);
                    }
                }
            }
        }
    }

    private void moveBlock(int worldX, int sourceY, int worldZ, int targetY) {
        Block source = world.getBlockAt(worldX, sourceY, worldZ);
        if (source.isEmpty()) {
            return;
        }
        BlockState snapshot = source.getState(false);
        BlockState relocated = snapshot.copy(new Location(world, worldX, targetY, worldZ));
        relocated.update(true, false);
        source.setType(Material.AIR, false);
    }

    private void finish(boolean cancelled) {
        if (!running.get()) {
            return;
        }
        if (!completionNotified.compareAndSet(false, true)) {
            return;
        }
        running.set(false);
        stopPlayerEvictionTask();
        storage.clear();

        if (cancelled) {
            sendToInitiator(ChatColor.RED + "WorldMover gestoppt: " + cancelReason);
            plugin.getLogger().warning("WorldMover vorzeitig gestoppt: " + cancelReason);
        } else {
            sendToInitiator(ChatColor.GREEN + "WorldMover abgeschlossen. Ben\u00f6tigte Zeit: "
                    + formatDuration(Duration.between(startedAt, Instant.now())));
            plugin.getLogger().info("WorldMover abgeschlossen f\u00fcr Welt " + world.getName());
        }

        completionHook.accept(this);
    }

    private void startPlayerEvictionTask() {
        stopPlayerEvictionTask();
        playerEvictionTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::enforcePlayerEvacuation, 0L, 100L);
    }

    private void stopPlayerEvictionTask() {
        if (playerEvictionTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(playerEvictionTaskId);
            playerEvictionTaskId = -1;
        }
    }

    private void enforcePlayerEvacuation() {
        for (Player player : world.getPlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            player.sendMessage(ChatColor.RED + "Diese Welt wird aktuell verschoben. Du wirst automatisch zur Lobby gesendet.");
            player.performCommand("lobby");
        }
    }

    private void maybeReportProgress(long done) {
        Instant now = Instant.now();
        if (done == totalChunks || Duration.between(lastProgressMessage, now).toMinutes() >= 1) {
            lastProgressMessage = now;
            sendToInitiator(describeProgress());
        }
    }

    private void persistProgress() {
        WorldMoveState snapshot = createStateSnapshot();
        if (snapshot == null) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> storage.save(snapshot));
    }

    private WorldMoveState createStateSnapshot() {
        int snapshotNextX;
        int snapshotNextZ;
        synchronized (traversalLock) {
            if (traversalFinished) {
                snapshotNextX = maxChunkX + 1;
                snapshotNextZ = maxChunkZ;
            } else {
                snapshotNextX = nextChunkX;
                snapshotNextZ = nextChunkZ;
            }
        }
        return new WorldMoveState(
                world.getName(),
                offset,
                minChunkX,
                maxChunkX,
                minChunkZ,
                maxChunkZ,
                snapshotNextX,
                snapshotNextZ,
                processedChunks.get(),
                totalChunks,
                startedAt.toEpochMilli(),
                initiatorName,
                initiatorUuid,
                initiatedByPlayer
        );
    }

    private ChunkCoordinate nextChunkCoordinate() {
        synchronized (traversalLock) {
            if (traversalFinished) {
                return null;
            }
            ChunkCoordinate coordinate = new ChunkCoordinate(nextChunkX, nextChunkZ);
            advanceCursor();
            return coordinate;
        }
    }

    private void advanceCursor() {
        if (nextChunkZ >= maxChunkZ) {
            nextChunkZ = minChunkZ;
            nextChunkX++;
            if (nextChunkX > maxChunkX) {
                traversalFinished = true;
            }
        } else {
            nextChunkZ++;
        }
    }

    private long computeTotalChunks() {
        long x = (long) (maxChunkX - minChunkX + 1);
        long z = (long) (maxChunkZ - minChunkZ + 1);
        return Math.max(0, x * z);
    }

    private void sendToInitiator(String message) {
        if (initiatedByPlayer && initiatorUuid != null) {
            Player player = Bukkit.getPlayer(initiatorUuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(message);
                return;
            }
        }
        if (initiator != null) {
            initiator.sendMessage(message);
        } else {
            plugin.getLogger().info(ChatColor.stripColor(message));
        }
    }

    private static String formatPercent(double value) {
        return String.format(Locale.GERMANY, "%.2f", value);
    }

    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        if (hours > 0) {
            return String.format(Locale.GERMANY, "%02d:%02d:%02d", hours, minutes, secs);
        }
        return String.format(Locale.GERMANY, "%02d:%02d", minutes, secs);
    }

    private String buildEta(long done, Duration elapsed) {
        if (done <= 0) {
            return "unbekannt";
        }
        long perChunkMillis = Math.max(1L, elapsed.toMillis() / done);
        long remainingChunks = Math.max(0L, totalChunks - done);
        if (remainingChunks == 0) {
            return "00:00";
        }
        long maxChunksBeforeOverflow = Long.MAX_VALUE / perChunkMillis;
        long safeRemainingChunks = Math.min(remainingChunks, maxChunksBeforeOverflow);
        long remainingMillis = perChunkMillis * safeRemainingChunks;
        return formatDuration(Duration.ofMillis(remainingMillis));
    }

    private static BorderWindow resolveBorder(World world) {
        WorldBorder border = world.getWorldBorder();
        double size = border.getSize();
        if (size > 5.9E7) {
            throw new IllegalStateException("Bitte setze vor dem WorldMover eine sinnvolle WorldBorder.");
        }
        Location center = border.getCenter();
        double half = size / 2.0D;
        double minX = center.getX() - half;
        double maxX = center.getX() + half;
        double minZ = center.getZ() - half;
        double maxZ = center.getZ() + half;

        int minChunkX = (int) Math.floor(minX / 16.0D);
        int maxChunkX = (int) Math.ceil(maxX / 16.0D);
        int minChunkZ = (int) Math.floor(minZ / 16.0D);
        int maxChunkZ = (int) Math.ceil(maxZ / 16.0D);

        return new BorderWindow(minChunkX, maxChunkX, minChunkZ, maxChunkZ);
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    World getWorld() {
        return world;
    }

    String getWorldName() {
        return world.getName();
    }

    private static final class BorderWindow {
        final int minChunkX;
        final int maxChunkX;
        final int minChunkZ;
        final int maxChunkZ;

        BorderWindow(int minChunkX, int maxChunkX, int minChunkZ, int maxChunkZ) {
            this.minChunkX = minChunkX;
            this.maxChunkX = maxChunkX;
            this.minChunkZ = minChunkZ;
            this.maxChunkZ = maxChunkZ;
        }
    }

    private record ChunkCoordinate(int x, int z) {
        @Override
        public String toString() {
            return x + "," + z;
        }
    }
}
