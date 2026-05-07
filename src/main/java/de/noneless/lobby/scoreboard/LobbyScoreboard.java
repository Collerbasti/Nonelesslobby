package de.noneless.lobby.scoreboard;

import Config.ConfigManager;
import de.noneless.lobby.util.LobbyAbilities;
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
        new CommandEntry("/warps", "Menü", "nonelesslobby.user"),
        new CommandEntry("/settings", "Optionen", "nonelesslobby.user"),
        new CommandEntry("/lobbynpc", "NPCs", "nonelesslobby.admin"),
        new CommandEntry("/worldmover", "Welten", "nonelesslobby.worldmover"),
        new CommandEntry("/lobbyreload", "Reload", "nonelesslobby.admin.reload"),
        new CommandEntry("/punkteadmin", "Punkte", "nonelesslobby.admin"),
        new CommandEntry("/friend", "Freunde", "nonelesslobby.friends"),
        new CommandEntry("/punkte", "Punkte", "nonelesslobby.leaderboard"),
        new CommandEntry("/nonelessgame:menu", "Spiele", null, "NonelessGame")
    };

    private static JavaPlugin plugin;
    private static BukkitTask autoUpdateTask;
    
    // Last player count is kept for compatibility with existing update triggers.
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
        score = addLine(objective, "§fWelt: §a" + player.getWorld().getName(), score);
        score = addLine(objective, "§8----------------", score);
        score = addLine(objective, "§bSchnellzugriff:", score);

        for (String cmd : getCommands(player)) {
            score = addLine(objective, "§e" + cmd, score);
        }

        score = addLine(objective, "§8----------------", score);
        score = addLine(objective, "§dStatus:", score);
        int activeAbilities = LobbyAbilities.getActiveAbilities(player.getUniqueId()).size();
        score = addLine(objective, "§fFähigkeiten: " + formatCount(activeAbilities), score);
        for (String custom : customLines) {
            if (custom == null || custom.isEmpty()) continue;
            String parsed = ChatColor.translateAlternateColorCodes('&', custom);
            score = addLine(objective, parsed, score);
        }

        player.setScoreboard(board);
    }

    public static void updateAll() {
        if (plugin == null) return;
        
        lastPlayerCount = Bukkit.getOnlinePlayers().size();
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
        lastPlayerCount = -1;
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
            if (entry.canUse(player)) {
                commands.add(entry.render());
                if (commands.size() >= 5) break;
            }
        }
        if (commands.isEmpty()) {
            commands.add("/settings §7Optionen");
        }
        return commands;
    }

    private static String formatCount(int value) {
        return value > 0 ? ChatColor.GREEN + String.valueOf(value) + " aktiv" : ChatColor.GRAY + "keine aktiv";
    }

    private static class CommandEntry {
        private final String command;
        private final String label;
        private final String permission;
        private final String requiredPlugin;

        CommandEntry(String command, String label, String permission) {
            this(command, label, permission, null);
        }

        CommandEntry(String command, String label, String permission, String requiredPlugin) {
            this.command = command;
            this.label = label;
            this.permission = permission;
            this.requiredPlugin = requiredPlugin;
        }

        boolean canUse(Player player) {
            if (requiredPlugin != null && !Bukkit.getPluginManager().isPluginEnabled(requiredPlugin)) {
                return false;
            }
            return permission == null || player.hasPermission(permission);
        }

        String render() {
            return command + " §7" + label;
        }
    }
    
    private static void startAutoUpdater() {
        if (plugin == null) return;
        if (autoUpdateTask != null) autoUpdateTask.cancel();
        long interval = 20L * 35; // ~35 Sekunden
        autoUpdateTask = Bukkit.getScheduler().runTaskTimer(plugin, LobbyScoreboard::updateAll, interval, interval);
    }
}
