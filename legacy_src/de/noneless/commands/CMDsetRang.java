package de.noneless.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.noneless.Mysql.Punkte;

public class CMDsetRang implements CommandExecutor  {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		Player p = (Player) sender;
		if (!p.hasPermission("Noneless.SetRang")) {
			p.sendMessage("Du darfst das nicht");
			return true;
		}
		if (args.length != 3) {
			p.sendMessage("Es fehlen Argumente: /setRang add/remove Player Punkte");
			return true;
		}
		Player target = Bukkit.getPlayer(args[1]);
		if (target == null) {
			p.sendMessage("Spieler nicht gefunden oder nicht online.");
			return true;
		}
		try {
			int punkte = Integer.parseInt(args[2]);
			if (args[0].equalsIgnoreCase("add")) {
				Punkte.Update(target.getUniqueId(), punkte, args[1], false, p);
				p.sendMessage("Der Spieler " + args[1] + " hat nun den Rang " + Punkte.getPoints(target.getUniqueId()));
			} else if (args[0].equalsIgnoreCase("remove")) {
				Punkte.Update(target.getUniqueId(), punkte, args[1], true, p);
				p.sendMessage("Der Spieler " + args[1] + " hat nun den Rang " + Punkte.getPoints(target.getUniqueId()));
			} else {
				p.sendMessage(args[0] + " ist ein ungültiges Argument");
			}
		} catch (NumberFormatException e) {
			p.sendMessage("Punkte müssen eine Zahl sein.");
		}
		return true;
	}
	
}
		


//setRang add/remove Player Punkte
