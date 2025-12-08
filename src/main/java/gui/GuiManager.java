package gui;

import org.bukkit.Bukkit;

public class GuiManager {
    
    public static void initialize() {
        Bukkit.getLogger().info("[GuiManager] Initialisierung...");
    }
    
    public static void shutdown() {
        Bukkit.getLogger().info("[GuiManager] Herunterfahren...");
    }
}