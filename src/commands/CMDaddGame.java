package commands;

import java.io.IOException;
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
				p.sendMessage("Das MiniSpiel Gibt es Leider nicht Das Angegebene Material wird als Game Material genutzt");
				MiniGames.add(args[0]);
				 Main.MiGm.set("Global.Minigames", MiniGames);
				 Main.MiGm.set(args[0]+".StartCommand","Bitte hier den Befehl Eingeben");
				 Main.MiGm.set("Global.Count",Main.MiGm.getInt("Global.Count") + 1);
				 
				 
				 
				 if(args[2]=="BED") {
						Main.MiGm.set(args[0]+".Mat", 1);
						}else if(args[2]=="WOOD") {
							Main.MiGm.set(args[0]+".Mat",2);
						}else {
							Main.MiGm.set(args[0]+".Mat", 3);
						}
				 
				 MiniGameArenas.add(args[1]);
				 
					Main.MiGm.set(args[0]+".Arenas",MiniGameArenas);
					int ACounter = Main.MiGm.getInt(args[0]+".Count");
					Main.MiGm.set(args[0]+".Count", ACounter + 1);
					
					Main.MiGm.set(args[0]+"."+args[1]+".Mat", args[2]);
					
			}
				
			}else { //if(args.length == 1)
			p.sendMessage("/addGame MinispielName ArenaName Material");
			
			
			
		}
		
		
		
	}else {
		p.sendMessage("Du Hast nicht das Recht dazu");
		
		
		
	}
		
		
	try {
		Main.MiGm.save(Main.Minigames);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return true;

		

	
	}

	}
