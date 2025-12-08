package de.noneless.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.noneless.lobby.Menues.FriendsMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CMDFriend implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("nonelesslobby.friends")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können das Freunde-Menü öffnen.");
            return true;
        }

        FriendsMenu menu = new FriendsMenu();
        menu.openMain((Player) sender);
        
        return true;
    }
}
