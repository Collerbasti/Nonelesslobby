package de.noneless.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDMySQLConnect implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		Player p = (Player) sender;
		if (!p.hasPermission("Noneless.MySQLConnect")) {
			p.sendMessage("Nein, du hast keine Berechtigung.");
			return true;
		}
		de.noneless.MySQL.connect();
		p.sendMessage("Du hast dich verbunden");
		return true;
	}
}
