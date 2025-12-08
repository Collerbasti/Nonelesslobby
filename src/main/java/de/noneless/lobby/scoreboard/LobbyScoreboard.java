package de.noneless.lobby.scoreboard;

import Config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class LobbyScoreboard {

    private static final String OBJECTIVE = "nlobby";
    private static final String TITLE = ChatColor.GREEN + "Noneless Lobby";
    private static final String[] customLines = new String[4];
    private static final CommandEntry[] COMMAND_ENTRIES = {
        new CommandEntry("/warps", null),
        new CommandEntry("/friend", "nonelesslobby.friends"),
        new CommandEntry("/settings", null),
        new CommandEntry("/lobbynpc", "nonelesslobby.admin"),
        new CommandEntry("/punkte", null),
        new CommandEntry("/punkteadmin", "nonelesslobby.punkte.admin")
    };

    private static JavaPlugin plugin;
    private static BukkitTask autoUpdateTask;
    
    // Cache last player count to avoid rebuilding scoreboards when nothing changed
    private static int lastPlayerCount = -1;
    private static final Map<UUID, Long> lastUpdateTime = new HashMap<>();
    private static final long MIN_UPDATE_INTERVAL = 5000L; // Min 5 seconds (5000ms) between individual updates

    private LobbyScoreboard() {}

    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        startAutoUpdater();
    }

    public static void update(Player player) {
        if (plugin == null) return;
        
        // Rate limiting: don't update same player too frequently
        UUID playerId = player.getUniqueId();
        Long lastUpdate = lastUpdateTime.get(playerId);
        long now = System.currentTimeMillis();
        if (lastUpdate != null && (now - lastUpdate) < MIN_UPDATE_INTERVAL) {
            return; // Skip update, too soon
        }
        lastUpdateTime.put(playerId, now);
        
        Location lobby = ConfigManager.getLobbyLocation();
        if (lobby == null || lobby.getWorld() == null) return;
        if (!player.getWorld().equals(lobby.getWorld())) {
            Scoreboard empty = Bukkit.getScoreboardManager() != null ?
                Bukkit.getScoreboardManager().getNewScoreboard() : null;
            if (empty != null) {
                player.setScoreboard(empty);
            }
            return;
        }
        
        updateScoreboard(player);
    }
    
    private static void updateScoreboard(Player player) {
        org.bukkit.scoreboard.ScoreboardManager sbManager = Bukkit.getScoreboardManager();
        if (sbManager == null) return;
        Scoreboard board = sbManager.getNewScoreboard();
        Objective objective = board.registerNewObjective(OBJECTIVE, "dummy", TITLE);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = 15;
        int currentPlayerCount = Bukkit.getOnlinePlayers().size();
        score = addLine(objective, "§fSpieler: §a" + currentPlayerCount, score);
        score = addLine(objective, "§8----------------", score);
        score = addLine(objective, "§bVerfügbare Befehle:", score);

        for (String cmd : getCommands(player)) {
            score = addLine(objective, "§e" + cmd, score);
        }

        score = addLine(objective, "§8----------------", score);
        score = addLine(objective, "§dInfo:", score);
        for (String custom : customLines) {
            if (custom == null || custom.isEmpty()) continue;
            String parsed = ChatColor.translateAlternateColorCodes('&', custom);
            score = addLine(objective, parsed, score);
        }

        player.setScoreboard(board);
    }

    public static void updateAll() {
        if (plugin == null) return;
        
        int currentPlayerCount = Bukkit.getOnlinePlayers().size();
        
        // Only update if player count changed or custom lines were modified
        if (lastPlayerCount == currentPlayerCount) {
            return; // Skip update, nothing changed
        }
        
        lastPlayerCount = currentPlayerCount;
        Bukkit.getOnlinePlayers().forEach(LobbyScoreboard::update);
    }
    
    public static void shutdown() {
        if (autoUpdateTask != null) {
            autoUpdateTask.cancel();
            autoUpdateTask = null;
        }
        lastUpdateTime.clear();
    }

    public static void setCustomLine(int index, String text) {
        if (plugin == null) return;
        if (index < 0 || index >= customLines.length) return;
        customLines[index] = text;
        lastPlayerCount = -1; // Force update on next updateAll()
        updateAll();
    }

    public static List<String> getCustomLines() {
        return Arrays.asList(customLines.clone());
    }

    private static int addLine(Objective objective, String text, int score) {
        if (score <= 0) return score;
        String unique = text + ChatColor.values()[score % ChatColor.values().length];
        objective.getScore(unique).setScore(score);
        return score - 1;
    }

    private static List<String> getCommands(Player player) {
        List<String> commands = new ArrayList<>();
        for (CommandEntry entry : COMMAND_ENTRIES) {
            if (entry.permission == null || player.hasPermission(entry.permission)) {
                commands.add(entry.command);
                if (commands.size() >= 4) break;
            }
        }
        if (commands.isEmpty()) {
            commands.add("/settings");
        }
        return commands;
    }

    private static class CommandEntry {
        private final String command;
        private final String permission;

        CommandEntry(String command, String permission) {
            this.command = command;
            this.permission = permission;
        }
    }
    
    private static void startAutoUpdater() {
        if (plugin == null) return;
        if (autoUpdateTask != null) autoUpdateTask.cancel();
        long interval = 20L * 35; // ~35 Sekunden
        autoUpdateTask = Bukkit.getScheduler().runTaskTimer(plugin, LobbyScoreboard::updateAll, interval, interval);
    }
}
