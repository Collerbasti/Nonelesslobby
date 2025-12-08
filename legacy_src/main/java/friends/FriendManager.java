package friends;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FriendManager {

    private static File friendFile;
    private static FileConfiguration friendConfig;
    private static boolean isDirty = false;
    private static BukkitTask autoSaveTask;
    private static final long AUTO_SAVE_INTERVAL = 20L * 60; // Save every 60 seconds

    public static void initialize(JavaPlugin plugin) {
        friendFile = new File(plugin.getDataFolder(), "friends.yml");
        if (!friendFile.exists()) {
            friendFile.getParentFile().mkdirs();
            try {
                friendFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Konnte friends.yml nicht erstellen: " + e.getMessage());
            }
        }
        friendConfig = YamlConfiguration.loadConfiguration(friendFile);
        
        // Start auto-save task to periodically save if dirty
        autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (isDirty) {
                saveNow();
            }
        }, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    public static void shutdown() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
            autoSaveTask = null;
        }
        saveNow(); // Force save on shutdown
    }

    /**
     * Mark configuration as dirty (needs saving)
     */
    private static void markDirty() {
        isDirty = true;
    }

    /**
     * Save immediately (synchronous)
     */
    private static void saveNow() {
        if (friendFile == null || friendConfig == null) {
            return;
        }
        try {
            friendConfig.save(friendFile);
            isDirty = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Deprecated: Use markDirty() instead for batched saves
     */
    @Deprecated
    public static void save() {
        saveNow();
    }

    public static List<String> getFriends(String playerName) {
        if (friendConfig == null) return Collections.emptyList();
        return new ArrayList<>(friendConfig.getStringList(playerKey(playerName)));
    }

    public static FriendRequestResult sendRequest(String requester, String target) {
        if (requester.equalsIgnoreCase(target)) {
            return FriendRequestResult.SELF;
        }
        if (areFriends(requester, target)) {
            return FriendRequestResult.ALREADY_FRIENDS;
        }
        List<String> pending = getPendingRequests(target);
        if (pending.stream().anyMatch(name -> name.equalsIgnoreCase(requester))) {
            return FriendRequestResult.ALREADY_PENDING;
        }
        pending.add(requester);
        friendConfig.set(requestKey(target), pending);
        markDirty(); // Changed from save()
        return FriendRequestResult.SENT;
    }

    public static List<String> getPendingRequests(String player) {
        if (friendConfig == null) return Collections.emptyList();
        return new ArrayList<>(friendConfig.getStringList(requestKey(player)));
    }

    public static boolean acceptRequest(String receiver, String requester) {
        List<String> pending = getPendingRequests(receiver);
        boolean removed = pending.removeIf(name -> name.equalsIgnoreCase(requester));
        if (!removed) return false;
        friendConfig.set(requestKey(receiver), pending);
        addFriend(receiver, requester);
        markDirty(); // Changed from save()
        return true;
    }

    public static boolean denyRequest(String receiver, String requester) {
        List<String> pending = getPendingRequests(receiver);
        boolean removed = pending.removeIf(name -> name.equalsIgnoreCase(requester));
        if (removed) {
            friendConfig.set(requestKey(receiver), pending);
            markDirty(); // Changed from save()
        }
        return removed;
    }

    private static void addFriend(String owner, String newFriend) {
        if (owner.equalsIgnoreCase(newFriend)) return;
        Set<String> ownerFriends = new HashSet<>(getFriends(owner));
        Set<String> targetFriends = new HashSet<>(getFriends(newFriend));
        if (ownerFriends.add(newFriend)) {
            friendConfig.set(playerKey(owner), new ArrayList<>(ownerFriends));
        }
        if (targetFriends.add(owner)) {
            friendConfig.set(playerKey(newFriend), new ArrayList<>(targetFriends));
        }
        markDirty(); // Changed from save()
    }

    public static void removeFriend(String owner, String friend) {
        Set<String> ownerFriends = new HashSet<>(getFriends(owner));
        if (ownerFriends.remove(friend)) {
            friendConfig.set(playerKey(owner), new ArrayList<>(ownerFriends));
        }

        Set<String> otherFriends = new HashSet<>(getFriends(friend));
        if (otherFriends.remove(owner)) {
            friendConfig.set(playerKey(friend), new ArrayList<>(otherFriends));
        }
        markDirty(); // Changed from save()
    }

    public static boolean areFriends(String a, String b) {
        return getFriends(a).stream().anyMatch(name -> name.equalsIgnoreCase(b));
    }

    private static String playerKey(String playerName) {
        return "players." + playerName.toLowerCase(Locale.ROOT);
    }

    private static String requestKey(String playerName) {
        return "requests." + playerName.toLowerCase(Locale.ROOT);
    }

    public enum FriendRequestResult {
        SENT,
        ALREADY_FRIENDS,
        ALREADY_PENDING,
        SELF
    }
}
