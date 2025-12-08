package de.noneless.lobby.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Coordinates long running world move jobs so only one can run at a time.
 */
public class WorldMoverService {

    private final JavaPlugin plugin;
    private final WorldMoverStateStorage storage;
    private final ReentrantLock stateLock = new ReentrantLock();
    private WorldMoveJob runningJob;

    public WorldMoverService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.storage = new WorldMoverStateStorage(plugin);
    }

    /**
     * Starts a new world move job. Only one job may run at a time.
     *
     * @throws IllegalStateException if a job is already running
     */
    public void startMove(CommandSender initiator, World world, int verticalOffset) {
        if (verticalOffset == 0) {
            throw new IllegalArgumentException("Offset may not be zero.");
        }

        stateLock.lock();
        try {
            ensureJobSlotsAvailable();
            runningJob = new WorldMoveJob(plugin, initiator, world, verticalOffset, storage, createCompletionHook(), null);
            runningJob.start();
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Attempts to resume a pending job from disk when the plugin boots.
     */
    public void resumePendingJob() {
        Optional<WorldMoveState> optionalState = storage.load();
        if (optionalState.isEmpty()) {
            return;
        }

        WorldMoveState state = optionalState.get();
        World world = Bukkit.getWorld(state.worldName());
        if (world == null) {
            world = Bukkit.createWorld(WorldCreator.name(state.worldName()));
        }
        if (world == null) {
            plugin.getLogger().log(Level.SEVERE, "Konnte WorldMover-Auftrag nicht fortsetzen. Welt " + state.worldName() + " ist nicht verf\u00fcgbar.");
            return;
        }

        CommandSender console = Bukkit.getConsoleSender();
        stateLock.lock();
        try {
            ensureJobSlotsAvailable();
            runningJob = new WorldMoveJob(plugin, console, world, state.offset(), storage, createCompletionHook(), state);
            runningJob.start();
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Fehler beim Fortsetzen des WorldMover-Auftrags.", ex);
            storage.clear();
        } finally {
            stateLock.unlock();
        }
    }

    public Optional<WorldMoveJob> getRunningJob() {
        stateLock.lock();
        try {
            return Optional.ofNullable(runningJob);
        } finally {
            stateLock.unlock();
        }
    }

    public Optional<String> getRestrictedWorldName() {
        stateLock.lock();
        try {
            if (runningJob == null) {
                return Optional.empty();
            }
            return Optional.of(runningJob.getWorldName());
        } finally {
            stateLock.unlock();
        }
    }

    public Optional<String> describeActiveJob() {
        stateLock.lock();
        try {
            if (runningJob == null) {
                return Optional.empty();
            }
            return Optional.of(runningJob.describeProgress());
        } finally {
            stateLock.unlock();
        }
    }

    public boolean isWorldRestricted(World world) {
        if (world == null) {
            return false;
        }
        return isWorldRestricted(world.getName());
    }

    public boolean isWorldRestricted(String worldName) {
        if (worldName == null) {
            return false;
        }
        stateLock.lock();
        try {
            return runningJob != null && runningJob.getWorldName().equalsIgnoreCase(worldName);
        } finally {
            stateLock.unlock();
        }
    }

    public boolean hasActiveJob() {
        return getRunningJob().isPresent();
    }

    public boolean cancelActiveJob(String reason) {
        stateLock.lock();
        try {
            if (runningJob == null) {
                return false;
            }
            runningJob.cancel(reason);
            return true;
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Called during plugin shutdown. We don't cancel the job to preserve the snapshot for the next start.
     */
    public void shutdown() {
        stateLock.lock();
        try {
            if (runningJob != null) {
                plugin.getLogger().info("WorldMover-Auftrag wird beim n\u00e4chsten Start fortgesetzt.");
                runningJob = null;
            }
        } finally {
            stateLock.unlock();
        }
    }

    private void ensureJobSlotsAvailable() {
        if (runningJob != null && runningJob.isRunning()) {
            throw new IllegalStateException("Es l\u00e4uft bereits ein Verschiebeauftrag.");
        }
    }

    private Consumer<WorldMoveJob> createCompletionHook() {
        return job -> {
            stateLock.lock();
            try {
                if (runningJob == job) {
                    runningJob = null;
                }
            } finally {
                stateLock.unlock();
            }
        };
    }
}
