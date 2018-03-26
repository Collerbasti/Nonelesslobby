package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Mysql.Punkte;

public class CMDsetRang implements CommandExecutor  {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	
	{
		Player p = (Player) sender;
	
		p.sendMessage(String.valueOf(args.length));
		
			if(p.hasPermission("Noneless.SetRang")) {
		if(args.length== 3) {
			
			if(args[0]=="add") {
				Punkte.Update(Bukkit.getPlayer(args[1]).getUniqueId(), Integer.parseInt(args[2]), args[1], false,p);
				p.sendMessage("Der Spieler "+args[1] +" hat nun den Rang "+Punkte.getPoints(Bukkit.getPlayer(args[1]).getUniqueId()));
			
			}else if (args[0]=="remove") {
				Punkte.Update(Bukkit.getPlayer(args[1]).getUniqueId(), Integer.parseInt(args[2]), args[1], true,p);	
				p.sendMessage("Der Spieler "+args[1] +" hat nun den Rang "+Punkte.getPoints(Bukkit.getPlayer(args[1]).getUniqueId()));
			}else {
				p.sendMessage("ungültige argumente");
			}
			
			
			
			
		}else {
			p.sendMessage("Es fehlen argumente : /setRang add/remove Player Punkte");
		}
			
			}else {
				p.sendMessage("Du darfst das nicht");
			}
		
		return false;
	}
		

}
//setRang add/remove Player Punkte