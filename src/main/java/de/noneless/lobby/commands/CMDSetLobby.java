package de.noneless.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import de.noneless.lobby.Main;

public class CMDSetLobby implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("nonelesslobby.admin.setspawn")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Command kann nur von Spielern ausgeführt werden!");
            return true;
        }
        
        Player player = (Player) sender;
        Location location = player.getLocation();
        
        // Speichere die Lobby-Position persistent
        Main.getInstance().setLobbyLocation(location);
        
        sender.sendMessage(ChatColor.GREEN + "Lobby-Spawn wurde gesetzt bei:");
        sender.sendMessage(ChatColor.YELLOW + "Welt: " + ChatColor.WHITE + location.getWorld().getName());
        sender.sendMessage(ChatColor.YELLOW + "Position: " + ChatColor.WHITE + 
                          String.format("%.1f, %.1f, %.1f", 
                          location.getX(), 
                          location.getY(), 
                          location.getZ()));
        sender.sendMessage(ChatColor.GREEN + "Lobby-Position wurde in der Config gespeichert!");
        
        return true;
    }
}
