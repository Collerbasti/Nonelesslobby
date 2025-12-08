package de.noneless.lobby.commands;

import friends.FriendRequestSession;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command: /testfriendrequest <name>
 * Admin-Command um die Freundschaftsanfrage-Ansicht zu testen
 */
public class CMDTestFriendRequest implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nonelesslobby.admin")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können Test-Anfragen empfangen.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.YELLOW + "Verwendung: /testfriendrequest <spielername>");
            return true;
        }

        Player player = (Player) sender;
        String requesterName = args[0];

        FriendRequestSession.showTestRequest(player, requesterName);
        player.sendMessage(ChatColor.GREEN + "Test-Anfrage von §e" + requesterName + " §agesendet!");

        return true;
    }
}
