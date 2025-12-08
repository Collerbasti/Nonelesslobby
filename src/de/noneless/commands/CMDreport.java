package de.noneless.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDreport implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		Player p = (Player) sender;
		int count = de.noneless.Main.rpt.getInt("Report.Count");
		if (args.length < 2) {
			p.sendMessage("Bitte gebe /report [Spieler] [Grund] ein");
			return true;
		}
		String target = args[0];
		StringBuilder grund = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			grund.append(args[i]);
			if (i < args.length - 1)
				grund.append(" ");
		}
		ArrayList<String> reportList = new ArrayList<>(de.noneless.Main.rpt.getStringList(target + ".List"));
		reportList.add(grund.toString());
		p.sendMessage("Der Report wird gesendet");
		de.noneless.Main.rpt.set(target + ".Reports." + (count + 1), p.getName());
		de.noneless.Main.rpt.set(target + ".List" + (count + 1), reportList);
		de.noneless.Main.rpt.set("Report.Count", count + 1);
		de.noneless.Main.rpt.set(target + ".Number", count + 1);
		p.sendMessage("Die Report Nummer lautet: " + (count + 1) + " Bitte für alle Fälle aufbewahren");
		try {
			de.noneless.Main.rpt.save(de.noneless.Main.Reports);
		} catch (IOException e) {
			p.sendMessage("Fehler beim Speichern: " + e.getMessage());
		}
		return true;
	}
}
