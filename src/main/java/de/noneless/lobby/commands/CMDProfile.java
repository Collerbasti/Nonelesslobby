package de.noneless.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

public class CMDProfile implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("nonelesslobby.profile")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command!");
            return true;
        }
        
        Player targetPlayer = null;
        
        if (args.length > 0) {
            // Zeige Profil eines anderen Spielers
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Spieler '" + args[0] + "' wurde nicht gefunden!");
                return true;
            }
        } else if (sender instanceof Player) {
            // Zeige eigenes Profil
            targetPlayer = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Du musst einen Spielernamen angeben!");
            return true;
        }
        
        // Zeige Profil-Informationen
        sender.sendMessage(ChatColor.GOLD + "=== Profil von " + targetPlayer.getName() + " ===");
        sender.sendMessage(ChatColor.YELLOW + "Online seit: " + ChatColor.WHITE + "Unbekannt");
        sender.sendMessage(ChatColor.YELLOW + "Punkte: " + ChatColor.GREEN + "0"); // Später mit Punkte-System verknüpfen
        sender.sendMessage(ChatColor.YELLOW + "Rang: " + ChatColor.AQUA + "Spieler");
        sender.sendMessage(ChatColor.GOLD + "================================");
        
        return true;
    }
}