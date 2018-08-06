package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDMySQLConnect implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if(command.getName().equals("connect"));
		if(sender instanceof Player) {
			if(!p.hasPermission("Noneless.MySQLConnect")) { 
			p.sendMessage("Nein böse");
			return true;
			
		}else {
			Main.MySQL.connect();
			p.sendMessage("Du hast dich Verbunden");
		}
		}
		return false;
	}
}
