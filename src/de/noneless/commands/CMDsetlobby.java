package de.noneless.commands;

import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		
	public class CMDsetlobby implements CommandExecutor {

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
				return true;
			}
			Player p = (Player) sender;
			if (!p.hasPermission("Noneless.lobby.set")) {
				p.sendMessage("§cDu darfst diesen Befehl nicht benutzen");
				return true;
			}
			if (args.length == 0) {
				p.sendMessage("Lobby wird gespeichert");
				Location loc = p.getLocation();
				de.noneless.Main.loc.set("spawn.X", loc.getX());
				de.noneless.Main.loc.set("spawn.Y", loc.getY());
				de.noneless.Main.loc.set("spawn.Z", loc.getZ());
				de.noneless.Main.loc.set("spawn.Yaw", loc.getYaw());
				de.noneless.Main.loc.set("spawn.Pitch", loc.getPitch());
				de.noneless.Main.loc.set("spawn.World", loc.getWorld().getName());
				try {
					de.noneless.Main.loc.save(de.noneless.Main.Locations);
				} catch (IOException e) {
					p.sendMessage("Fehler beim Speichern: " + e.getMessage());
				}
			} else if (args.length == 1) {
				p.sendMessage(args[0] + " wird gespeichert");
				Location loc = p.getLocation();
				de.noneless.Main.loc.set(args[0] + ".true", "true");
				de.noneless.Main.loc.set(args[0] + ".X", loc.getX());
				de.noneless.Main.loc.set(args[0] + ".Y", loc.getY());
				de.noneless.Main.loc.set(args[0] + ".Z", loc.getZ());
				de.noneless.Main.loc.set(args[0] + ".Yaw", loc.getYaw());
				de.noneless.Main.loc.set(args[0] + ".Pitch", loc.getPitch());
				de.noneless.Main.loc.set(args[0] + ".World", loc.getWorld().getName());
				try {
					de.noneless.Main.loc.save(de.noneless.Main.Locations);
				} catch (IOException e) {
					p.sendMessage("Fehler beim Speichern: " + e.getMessage());
				}
			} else {
				p.sendMessage("Verwendung: /setlobby [Name]");
			}
			return true;
		}
	}
