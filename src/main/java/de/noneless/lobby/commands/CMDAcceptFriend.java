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
 * Command: /annehmen
 * Akzeptiert eine ausstehende Freundschaftsanfrage
 */
public class CMDAcceptFriend implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können Freundschaftsanfragen annehmen.");
            return true;
        }

        Player player = (Player) sender;
        FriendRequestSession.FriendRequestData session = FriendRequestSession.getSession(player);

        if (session == null) {
            player.sendMessage(ChatColor.RED + "Du hast keine ausstehenden Freundschaftsanfragen.");
            return true;
        }

        String requester = session.requester;
        boolean accepted = FriendManager.acceptRequest(player.getName(), requester);

        if (accepted) {
            FriendRequestSession.endSession(player);
            player.sendMessage(ChatColor.GREEN + "✔ Du hast " + requester + " als Freund hinzugefügt!");

            // Benachrichtige den Anfragsteller, wenn er online ist
            Player requesterPlayer = Bukkit.getPlayer(requester);
            if (requesterPlayer != null && requesterPlayer.isOnline()) {
                requesterPlayer.sendMessage(ChatColor.GREEN + "✔ " + player.getName() + " hat deine Freundschaftsanfrage akzeptiert!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "✘ Fehler beim Annehmen der Anfrage.");
        }

        return true;
    }
}
