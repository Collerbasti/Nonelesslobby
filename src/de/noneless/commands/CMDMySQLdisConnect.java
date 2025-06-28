package de.noneless.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDMySQLdisConnect implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		Player p = (Player) sender;
		if (!p.hasPermission("Noneless.MySQLdisConnect")) {
			p.sendMessage("Nein, du hast keine Berechtigung.");
			return true;
		}
		de.noneless.MySQL.disconnect();
		p.sendMessage("Du hast dich getrennt");
		return true;
	}
}
