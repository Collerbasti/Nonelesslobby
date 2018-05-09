package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDdatabasedisconnect implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)  
	{
		
		Player p = (Player) sender;
		if(sender instanceof Player) {
			p.sendMessage("Datenbank wird getrennt");
			Main.MySQL.disconnect();
		}else {
			return false;
		}
		return true;
	}

}
