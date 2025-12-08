package de.noneless.lobby.commands;

import de.noneless.lobby.Main;
import de.noneless.lobby.Menues.Warps;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class CMDWarps implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Command kann nur von Spielern ausgefuehrt werden!");
            return true;
        }

        // Wenn ein Argument vorhanden ist, prüfe ob es ein Essentials Warp ist
        if (args.length > 0) {
            String warpName = args[0];
            
            // Versuche den Essentials Warp Command auszuführen
            boolean success = player.performCommand("essentials:warp " + warpName);
            
            if (success) {
                return true;
            }
            
            // Falls der Essentials Warp nicht existiert, öffne das normale Menü
        }

        // Öffne das Warps-Menü
        try {
            new Warps().Spawn(player);
        } catch (Exception ex) {
            Main.getInstance().getLogger().log(Level.WARNING, "Konnte Warps-Menue nicht oeffnen.", ex);
            sender.sendMessage(ChatColor.GOLD + "=== Verfuegbare Warps ===");
            sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.WHITE + "City - Hauptstadt");
            sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.WHITE + "SkyBlock - SkyBlock Welt");
            sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.WHITE + "Games - Minispiele");
            sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.WHITE + "Creative - Kreativ Welt");
            sender.sendMessage(ChatColor.GRAY + "Warps-Menue wird implementiert...");
            sender.sendMessage(ChatColor.GOLD + "========================");
        }

        return true;
    }
}
