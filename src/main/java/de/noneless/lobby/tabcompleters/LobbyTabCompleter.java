package de.noneless.lobby.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        String commandName = command.getName().toLowerCase();
        
        switch (commandName) {
            case "lobby":
            case "spawn":
            case "hub":
                return new ArrayList<>(); // Keine Parameter
                
            case "setlobby":
                return new ArrayList<>(); // Keine Parameter
                
            case "profile":
            case "stats":
            case "info":
                return getOnlinePlayerNames(args[args.length - 1]);
                
            case "punkte":
            case "points":
            case "leaderboard":
            case "ranking":
                return new ArrayList<>(); // Keine Parameter
                
            case "warps":
            case "warp":
                if (args.length == 1) {
                    return filterList(getEssentialsWarps(), args[0]);
                }
                return new ArrayList<>();
                
            case "punkteadmin":
                if (args.length == 1) {
                    return filterList(Arrays.asList("add", "remove", "set", "reset", "list"), args[0]);
                }
                if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set"))) {
                    return getOnlinePlayerNames(args[1]);
                }
                if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set"))) {
                    return filterList(Arrays.asList("10", "50", "100", "500", "1000"), args[2]);
                }
                return new ArrayList<>();
                
            case "punktegeben":
                if (args.length == 1) {
                    return getOnlinePlayerNames(args[0]);
                }
                if (args.length == 2) {
                    return filterList(Arrays.asList("10", "50", "100", "500", "1000"), args[1]);
                }
                return new ArrayList<>();
                
            case "friend":
            case "friends":
            case "f":
                if (args.length == 1) {
                    return filterList(Arrays.asList("add", "remove", "list", "accept", "deny", "tp", "online"), args[0]);
                }
                if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("tp"))) {
                    return getOnlinePlayerNames(args[1]);
                }
                return new ArrayList<>();
                
            case "lobbyreload":
                return new ArrayList<>(); // Keine Parameter
                
            case "setrank":
                if (args.length == 1) {
                    return getOnlinePlayerNames(args[0]);
                }
                if (args.length == 2) {
                    return filterList(Arrays.asList("Admin", "Moderator", "VIP", "User", "Builder", "Helper"), args[1]);
                }
                return new ArrayList<>();
                
            case "serverinfo":
                return new ArrayList<>(); // Keine Parameter
                
            case "help":
            case "hilfe":
            case "commands":
                if (args.length == 1) {
                    return filterList(Arrays.asList("1", "2", "3", "4", "5"), args[0]);
                }
                return new ArrayList<>();
                
            case "report":
                if (args.length == 1) {
                    return getOnlinePlayerNames(args[0]);
                }
                if (args.length == 2) {
                    return filterList(Arrays.asList("Hacking", "Griefing", "Spam", "Beleidigung", "Werbung", "Sonstiges"), args[1]);
                }
                return new ArrayList<>();
                
            case "vote":
                return new ArrayList<>(); // Keine Parameter
                
            case "lobbynpc":
            case "npc":
            case "npcs":
                if (args.length == 1) {
                    return filterList(Arrays.asList("spawn", "remove", "reload", "count", "setlobby", "info", "list"), args[0]);
                }
                return new ArrayList<>();
                
            case "settings":
            case "einstellungen":
            case "config":
                return new ArrayList<>(); // Keine Parameter
                
            default:
                return new ArrayList<>();
        }
    }
    
    /**
     * Gibt eine Liste aller Online-Spieler zurück, gefiltert nach dem eingegebenen Argument
     */
    private List<String> getOnlinePlayerNames(String arg) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Holt alle verfügbaren Essentials-Warps
     */
    private List<String> getEssentialsWarps() {
        List<String> warps = new ArrayList<>();
        try {
            // Versuche, Essentials zu finden
            if (!Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                // Fallback: Hardcodierte Liste wenn Essentials nicht verfügbar
                return Arrays.asList("spawn");
            }
            
            // Versuche, die Essentials Warps zu laden
            Class<?> essentialsClass = Class.forName("com.earth2me.essentials.Essentials");
            Object essentialsInstance = Bukkit.getPluginManager().getPlugin("Essentials");
            
            if (essentialsInstance != null) {
                // Versuche auf die Warps zuzugreifen über Reflection
                try {
                    var warpManagerField = essentialsClass.getDeclaredMethod("getWarps");
                    warpManagerField.setAccessible(true);
                    Object warpManager = warpManagerField.invoke(essentialsInstance);
                    
                    if (warpManager != null) {
                        var getWarpListMethod = warpManager.getClass().getDeclaredMethod("getList");
                        getWarpListMethod.setAccessible(true);
                        Object warpList = getWarpListMethod.invoke(warpManager);
                        
                        if (warpList instanceof java.util.Collection) {
                            for (Object warp : (java.util.Collection<?>) warpList) {
                                if (warp != null) {
                                    warps.add(warp.toString());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Fallback auf Dateibasierte Warps
                    warps.addAll(getWarpsFromFile());
                }
            }
        } catch (Exception e) {
            // Fallback: Standard-Warps
            warps = Arrays.asList("spawn");
        }
        
        return warps.isEmpty() ? Arrays.asList("spawn") : warps;
    }
    
    /**
     * Versucht, Warps aus der Essentials Konfigurationsdatei zu laden
     */
    private List<String> getWarpsFromFile() {
        List<String> warps = new ArrayList<>();
        try {
            var essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
            if (essentialsPlugin == null) {
                return warps;
            }
            java.io.File essentialsDir = essentialsPlugin.getDataFolder();
            java.io.File warpsFile = new java.io.File(essentialsDir, "warps.yml");
            
            if (warpsFile.exists()) {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(warpsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("  ") || line.startsWith("\t")) {
                        continue;
                    }
                    if (line.contains(":")) {
                        String warpName = line.split(":")[0].trim();
                        if (!warpName.isEmpty() && !warpName.startsWith("#")) {
                            warps.add(warpName);
                        }
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            // Stille Exception - verwende Fallback
        }
        return warps;
    }
    
    /**
     * Filtert eine Liste basierend auf dem eingegebenen Argument
     */
    private List<String> filterList(List<String> list, String arg) {
        return list.stream()
                .filter(item -> item.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
}

