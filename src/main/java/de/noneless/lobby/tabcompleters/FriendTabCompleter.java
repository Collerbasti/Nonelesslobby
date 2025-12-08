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

public class FriendTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            // Erste Argument: Subcommands
            return filterList(Arrays.asList(
                "add", "remove", "list", "accept", "deny", "tp", "online", "help"
            ), args[0]);
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "add":
                case "remove":
                case "tp":
                    // Online Spieler anzeigen (außer sich selbst)
                    Player player = (Player) sender;
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> !name.equals(player.getName()))
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                            
                case "accept":
                case "deny":
                    // TODO: Hier könnten offene Freundschaftsanfragen angezeigt werden
                    return filterList(Arrays.asList("all"), args[1]);
                    
                default:
                    return new ArrayList<>();
            }
        }
        
        return new ArrayList<>();
    }
    
    private List<String> filterList(List<String> list, String arg) {
        return list.stream()
                .filter(item -> item.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
}

