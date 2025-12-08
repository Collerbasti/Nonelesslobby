package de.noneless.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class CMDSettings implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Command kann nur von Spielern ausgeführt werden!");
            return true;
        }
        
        Player player = (Player) sender;
        
        try {
            // Öffne das Settings-Menü
            Class<?> settingsClass = Class.forName("de.noneless.lobby.Menues.Settings");
            Object settingsInstance = settingsClass.newInstance();
            settingsClass.getMethod("Spawn", Player.class).invoke(settingsInstance, player);
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Fehler beim Öffnen der Einstellungen!");
            sender.sendMessage(ChatColor.GRAY + "Das Settings-System wird noch eingerichtet...");
        }
        
        return true;
    }
}