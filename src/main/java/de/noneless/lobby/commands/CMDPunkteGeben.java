package de.noneless.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class CMDPunkteGeben implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("nonelesslobby.admin")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command!");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== Punkte Geben ===");
        sender.sendMessage(ChatColor.GRAY + "Punkte-Geben Command wird später implementiert.");
        sender.sendMessage(ChatColor.GOLD + "====================");
        
        return true;
    }
}