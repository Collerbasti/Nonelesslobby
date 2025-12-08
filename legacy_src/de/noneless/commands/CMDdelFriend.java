package de.noneless.commands;





import java.io.IOException;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		




 
	public class CMDdelFriend implements CommandExecutor  
	{
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
				return true;
			}
			Player p = (Player) sender;
			if (args.length != 1) {
				p.sendMessage("§4/delfriend (freund)");
				return true;
			}
			ArrayList<String> Friends = new ArrayList<>(de.noneless.Main.Frdb.getStringList(p.getName() + ".Friends"));
			if (Friends.contains(args[0])) {
				int Counter = de.noneless.Main.Frdb.getInt(p.getName() + ".Count");
				Friends.remove(args[0]);
				de.noneless.Main.Frdb.set(p.getName() + ".Friends", Friends);
				de.noneless.Main.Frdb.set(p.getName() + ".Count", Counter - 1);
				p.sendMessage("Du bist nicht mehr mit " + args[0] + " befreundet");
				try {
					de.noneless.Main.Frdb.save(de.noneless.Main.Friends);
				} catch (IOException e) {
					p.sendMessage("Fehler beim Speichern der Freundesliste: " + e.getMessage());
				}
			} else {
				p.sendMessage("Du bist nicht mit " + args[0] + " befreundet");
			}
			return true;
		}
}
