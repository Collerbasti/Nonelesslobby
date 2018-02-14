package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDtest implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
	if(command.getName().equals("test")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
					if(args.length == 0) {
  p.sendMessage("Dies ist ein test!!!");
				
					} else {
						p.sendMessage("§aBitte überprüfe deine Eingabe!!!");
					}
				}
			}
		
			return false;
	}

}