package de.noneless.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.noneless.Main;

public class CMDspawn implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("spawn")) {
			if (args.length == 0) {
				Double x = Main.loc.getDouble("spawn.X");
				Double y = Main.loc.getDouble("spawn.Y");
				Double z = Main.loc.getDouble("spawn.Z");
				Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
				Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
				if (w == null) {
					p.sendMessage("Spawn-Welt nicht gefunden!");
					return true;
				}
				p.teleport(new Location(w, x, y, z, yaw, pitch));
				return true;
			} else if (args.length == 1) {
				String trueTest = Main.loc.getString(args[0] + ".true");
				if (trueTest == null) {
					p.sendMessage(args[0] + " existiert nicht");
				} else {
					Double x = Main.loc.getDouble(args[0] + ".X");
					Double y = Main.loc.getDouble(args[0] + ".Y");
					Double z = Main.loc.getDouble(args[0] + ".Z");
					Float yaw = (float) Main.loc.getDouble(args[0] + ".Yaw");
					Float pitch = (float) Main.loc.getDouble(args[0] + ".Pitch");
					org.bukkit.World w = Bukkit.getWorld(Main.loc.getString(args[0] + ".World"));
					if (w == null) {
						p.sendMessage("Welt nicht gefunden!");
						return true;
					}
					p.teleport(new Location(w, x, y, z, yaw, pitch));
				}
				return true;
			}
		}
		return false;
	}

}
