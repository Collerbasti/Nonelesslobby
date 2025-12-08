package de.noneless.lobby.commands;

import friends.FriendManager;
import friends.FriendRequestSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command: /ablehnen
 * Lehnt eine ausstehende Freundschaftsanfrage ab
 */
public class CMDDenyFriend implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können Freundschaftsanfragen ablehnen.");
            return true;
        }

        Player player = (Player) sender;
        FriendRequestSession.FriendRequestData session = FriendRequestSession.getSession(player);

        if (session == null) {
            player.sendMessage(ChatColor.RED + "Du hast keine ausstehenden Freundschaftsanfragen.");
            return true;
        }

        String requester = session.requester;
        boolean denied = FriendManager.denyRequest(player.getName(), requester);

        if (denied) {
            FriendRequestSession.endSession(player);
            player.sendMessage(ChatColor.RED + "✘ Du hast die Freundschaftsanfrage von " + requester + " abgelehnt.");

            // Benachrichtige den Anfragsteller, wenn er online ist
            Player requesterPlayer = Bukkit.getPlayer(requester);
            if (requesterPlayer != null && requesterPlayer.isOnline()) {
                requesterPlayer.sendMessage(ChatColor.RED + "✘ " + player.getName() + " hat deine Freundschaftsanfrage abgelehnt.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "✘ Fehler beim Ablehnen der Anfrage.");
        }

        return true;
    }
}
