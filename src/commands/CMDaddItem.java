package commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		
	public class CMDaddItem implements CommandExecutor {
		
		@SuppressWarnings("deprecation")
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			ArrayList<String> ShopsPreise = new ArrayList<String>();

				if(sender instanceof Player) {
					if(args.length == 1);
					Player p = (Player) sender;
					if(command.getName().equals("addItem"));
					if(!p.hasPermission("Noneless.addItem")) {
						p.sendMessage("§cDu darfst diesen Befehl nicht Benutzen");
					 return true;
					 
					}else {
					  
					  p.getInventory().getItemInMainHand().getType().getId();	
					  int C2 = Main.Main.Frdb.getInt(p.getItemInHand()+".Count");
					  Main.Main.shp.set(p.getItemInHand()+".ShopsPreise", ShopsPreise);
					  Main.Main.shp.set(p.getItemInHand()+".Count", C2+1);
					  p.sendMessage("Item wurde Gespeichert");
					
					try {
					Main.Main.shp.save(Main.Main.ShopsPreise);
				}catch (IOException e) {
					e.printStackTrace();
					
				}
				}	
					return true;
								
		}
				return true;
			}
}
