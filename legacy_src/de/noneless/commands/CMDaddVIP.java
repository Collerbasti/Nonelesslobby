package de.noneless.commands;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.noneless.Main;

public class CMDaddVIP implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			sender.sendMessage("§4/addvip (Spielername)");
			return true;
		}
		String playerName = args[0];
		Date time = (Date) de.noneless.Main.Frdb.get(playerName + ".VIP.Expression");
		if (de.noneless.Main.Frdb.getBoolean(playerName + ".VIP.Enable") && time != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(time);
			cal.add(Calendar.MONTH, 1);
			de.noneless.Main.Frdb.set(playerName + ".VIP.Expression", cal.getTime());
		} else {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);
			de.noneless.Main.Frdb.set(playerName + ".VIP.Enable", true);
			de.noneless.Main.Frdb.set(playerName + ".VIP.Expression", cal.getTime());
		}
		sender.sendMessage("VIP für " + playerName + " wurde um einen Monat verlängert oder gesetzt.");
		return true;
	}

}
