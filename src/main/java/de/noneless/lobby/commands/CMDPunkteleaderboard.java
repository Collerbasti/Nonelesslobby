package de.noneless.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.noneless.lobby.Main;

import java.util.*;
import java.util.stream.Collectors;

public class CMDPunkteleaderboard implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("nonelesslobby.leaderboard")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command!");
            return true;
        }
        
        Main plugin = Main.getInstance();
        
        // Sammle alle Spieler mit ihren Punkten
        Map<String, Integer> playerPoints = new HashMap<>();
        
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int points = plugin.getPoints(onlinePlayer.getUniqueId());
            if (points > 0) {
                playerPoints.put(onlinePlayer.getName(), points);
            }
        }
        
        // Zeige Punkte-Leaderboard
        sender.sendMessage(ChatColor.GOLD + "============= " + ChatColor.YELLOW + "PUNKTE LEADERBOARD" + ChatColor.GOLD + " =============");
        
        if (playerPoints.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "Noch keine Spieler mit Punkten vorhanden.");
        } else {
            // Sortiere nach Punkten (absteigend)
            List<Map.Entry<String, Integer>> sortedEntries = playerPoints.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10) // Top 10
                .collect(Collectors.toList());
            
            int rank = 1;
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                String rankColor = getRankColor(rank);
                sender.sendMessage(rankColor + "#" + rank + " " + ChatColor.WHITE + entry.getKey() + 
                                 ChatColor.GRAY + " - " + ChatColor.GOLD + entry.getValue() + " Punkte");
                rank++;
            }
        }
        
        // Zeige eigene Punkte (falls Spieler)
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int ownPoints = plugin.getPoints(player.getUniqueId());
            sender.sendMessage(ChatColor.GOLD + "=================================================");
            sender.sendMessage(ChatColor.YELLOW + "Deine Punkte: " + ChatColor.GOLD + ownPoints);
        }
        
        sender.sendMessage(ChatColor.GOLD + "=================================================");
        
        return true;
    }
    
    private String getRankColor(int rank) {
        switch (rank) {
            case 1: return ChatColor.GOLD.toString();
            case 2: return ChatColor.GRAY.toString();
            case 3: return ChatColor.YELLOW.toString();
            default: return ChatColor.WHITE.toString();
        }
    }
}