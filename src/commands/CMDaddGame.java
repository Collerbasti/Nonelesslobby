package commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Main.Main;

public class CMDaddGame implements CommandExecutor


{
	//addGame MinispielName ArenaName Material
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	
	{
		
	Player p = (Player) sender;
	if(p.hasPermission("Noneless.Admin.AddGame")) {
		
		if(args.length == 3) {
			ArrayList<String> MiniGames = new ArrayList<String>();
			ArrayList<String> MiniGameArenas = new ArrayList<String>();
			MiniGames.addAll(Main.MiGm.getStringList("Global.Minigames"));
			if(MiniGames.contains(args[0])) {
				
				MiniGameArenas.addAll(Main.MiGm.getStringList(args[0]+".Arenas"));
				if(MiniGameArenas.contains(args[1])) {
					p.sendMessage("Arena ist Bereits Gelistet");
				}else {
					MiniGameArenas.add(args[1]);
					Main.MiGm.set(args[0]+".Arenas",MiniGameArenas);
					int ACounter = Main.MiGm.getInt(args[0]+".Count");
					Main.MiGm.set(args[0]+".Count", ACounter + 1);
					Main.MiGm.set(args[0]+"."+args[1]+".Mat", args[2]);
					
				}
			}	else { //if(MiniGames.contains(args[0]))
				p.sendMessage("Das MiniSpiel Gibt es Leider nicht ");
			}
				
			}else { //if(args.length == 1)
			p.sendMessage("/addGame MinispielName ArenaName Material");
			
			
			
		}
		
		
		
	}else {
		p.sendMessage("Du Hast nicht das Recht dazu");
		
		
		
	}
		
		
		
		return true;
		
	}
	

	}
