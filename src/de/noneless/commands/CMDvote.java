package de.noneless.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDvote implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		Player p = (Player) sender;
		p.sendMessage("§4Hier sind die Links zum Voten : "
				+ "§ahttps://www.minecraft-serverlist.net/vote/46735");
		p.sendMessage("§ahttps://minecraft-server.eu/vote/index/128837");
		 
		return true;
	}

}
