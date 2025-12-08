package player;

import org.bukkit.Bukkit;

public class PlayerManager {
    
    public static void initialize() {
        Bukkit.getLogger().info("[PlayerManager] Initialisierung...");
    }
    
    public static void shutdown() {
        Bukkit.getLogger().info("[PlayerManager] Herunterfahren...");
    }
}