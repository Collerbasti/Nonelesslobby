package de.noneless.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import Config.ConfigManager;

public class CMDLobby implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Command kann nur von Spielern ausgeführt werden!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Hole gespeicherte Lobby-Position aus der Config
        Location lobbyLocation = ConfigManager.getLobbyLocation();
        
        if (lobbyLocation == null || lobbyLocation.getWorld() == null) {
            // Fallback: Standard Lobby-Position
            lobbyLocation = new Location(Bukkit.getWorld("world"), 0.5, 65, 0.5);
            player.sendMessage(ChatColor.YELLOW + "Keine Lobby-Position gesetzt! Verwende Standard-Position.");
            player.sendMessage(ChatColor.GRAY + "Admin kann mit /setlobby eine Position setzen.");
        }
        
        player.teleport(lobbyLocation);
        player.sendMessage(ChatColor.GREEN + "Du wurdest zur Lobby teleportiert!");
        
        return true;
    }
}