package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDvote implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(cmd.getName().equals("vote"));
		p.sendMessage("§4Hier ist der Link zum Voten : "
				+ "§ahttps://www.minecraft-serverlist.net/vote/46735");
		
		return false;
	}

}
