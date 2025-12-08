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
                    return filterList(Arrays.asList("city", "spawn", "arena", "shops", "pvp"), args[0]);
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
     * Filtert eine Liste basierend auf dem eingegebenen Argument
     */
    private List<String> filterList(List<String> list, String arg) {
        return list.stream()
                .filter(item -> item.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
}

