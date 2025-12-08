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

public class AdminTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        String commandName = command.getName().toLowerCase();
        
        switch (commandName) {
            case "punkteadmin":
                return handlePunkteAdmin(args);
                
            case "punktegeben":
                return handlePunkteGeben(args);
                
            case "setrank":
                return handleSetRank(args);
                
            default:
                return new ArrayList<>();
        }
    }
    
    private List<String> handlePunkteAdmin(String[] args) {
        if (args.length == 1) {
            return filterList(Arrays.asList(
                "add", "remove", "set", "reset", "list", "top", "help"
            ), args[0]);
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "add":
                case "remove":
                case "set":
                case "reset":
                    return getOnlinePlayerNames(args[1]);
                    
                case "list":
                    return filterList(Arrays.asList("all", "online", "top10"), args[1]);
                    
                default:
                    return new ArrayList<>();
            }
        }
        
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("add") || subCommand.equals("remove") || subCommand.equals("set")) {
                return filterList(Arrays.asList(
                    "1", "5", "10", "25", "50", "100", "250", "500", "1000", "2500", "5000"
                ), args[2]);
            }
        }
        
        return new ArrayList<>();
    }
    
    private List<String> handlePunkteGeben(String[] args) {
        if (args.length == 1) {
            return getOnlinePlayerNames(args[0]);
        }
        
        if (args.length == 2) {
            return filterList(Arrays.asList(
                "1", "5", "10", "25", "50", "100", "250", "500", "1000"
            ), args[1]);
        }
        
        return new ArrayList<>();
    }
    
    private List<String> handleSetRank(String[] args) {
        if (args.length == 1) {
            return getOnlinePlayerNames(args[0]);
        }
        
        if (args.length == 2) {
            return filterList(Arrays.asList(
                "Admin", "Moderator", "Helper", "Builder", "VIP", "Premium", "User", "Guest"
            ), args[1]);
        }
        
        return new ArrayList<>();
    }
    
    private List<String> getOnlinePlayerNames(String arg) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<String> filterList(List<String> list, String arg) {
        return list.stream()
                .filter(item -> item.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
}

