package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDMySQLdisConnect implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if(command.getName().equals("disconnect"));
		if(sender instanceof Player) {
			if(!p.hasPermission("Noneless.MySQLdisConnect")) {
			p.sendMessage("Nein böse");
			return true;
			
		}else {
			Main.MySQL.disconnect();
			p.sendMessage("Du hast dich Getrennt");
		}
		}
		return false;
	}
}
