package de.noneless.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

public class CMDServerInfo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        sender.sendMessage(ChatColor.GOLD + "=== Server Informationen ===");
        sender.sendMessage(ChatColor.YELLOW + "Server: " + ChatColor.WHITE + "Noneless Lobby");
        sender.sendMessage(ChatColor.YELLOW + "Online Spieler: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
        sender.sendMessage(ChatColor.YELLOW + "Max Spieler: " + ChatColor.WHITE + Bukkit.getMaxPlayers());
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + Bukkit.getVersion());
        sender.sendMessage(ChatColor.GOLD + "===========================");
        
        return true;
    }
}