package de.noneless.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.noneless.Main;
		
	public class CMDremoveKIcommand implements CommandExecutor {

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
				return true;
			}
			Player p = (Player) sender;
			if (!p.hasPermission("Noneless.GlaDOS")) {
				p.sendMessage("Du hast keine Berechtigung.");
				return true;
			}
			if (args.length != 1) {
				p.sendMessage("Verwendung: /removeKIcommand <KICommand>");
				return true;
			}
			ArrayList<String> glados = new ArrayList<>(Main.GDOS.getStringList("GlaDOS.List"));
			if (glados.contains(args[0])) {
				glados.remove(args[0]);
				p.sendMessage(args[0] + " wurde erfolgreich gelöscht");
				Main.GDOS.set("GlaDOS.List", glados);
				p.sendMessage(String.valueOf(Main.GDOS.get("GlaDOS." + args[0] + ".Player")) + " war es übrigens");
				Main.GDOS.set("GlaDOS." + args[0] + ".answer", null);
				Main.GDOS.set("GlaDOS." + args[0] + ".Player", null);
			} else {
				p.sendMessage(args[0] + " wurde nicht gefunden.");
			}
			return true;
		}
			

}
//removeKIcommand (KICommand)
