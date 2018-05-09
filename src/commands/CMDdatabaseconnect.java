package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDdatabaseconnect implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)  
	{
		Player p = (Player) sender;
		if(sender instanceof Player) {
			p.sendMessage("Datenbank wird verbunden");
			Main.MySQL.connect();
		}else {
			return false;
		}
		return true;
		
	}

}
