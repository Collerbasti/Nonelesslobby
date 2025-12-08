package de.noneless.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.noneless.Main;

public class CMDHilfe implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Nur für Spieler sinnvoll
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		final int HOURS_TO_CHECK = 12;
		final int HOURS_PER_DAY = 24;
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (players.hasPermission("Noneless.Admin")) {
				Date date = new Date();
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(date);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int[] counter = new int[HOURS_TO_CHECK];
				for (int i = 0; i < HOURS_TO_CHECK; i++) {
					int h = (hour + i) % HOURS_PER_DAY;
					counter[i] = de.noneless.Main.AOnline.getInt(players.getName() + "." + h);
				}
				int maxCounter = counter[0];
				int maxHour = hour;
				for (int i = 1; i < HOURS_TO_CHECK; i++) {
					if (counter[i] > maxCounter) {
						maxCounter = counter[i];
						maxHour = (hour + i) % HOURS_PER_DAY;
					}
				}
				sender.sendMessage(players.getName() + " ist wahrscheinlich so gegen " + maxHour + ":00 online");
				if (sender.equals(players)) {
					sender.sendMessage(maxCounter + " Pnk");
				}
			}
		}
		return true;
	}

}
