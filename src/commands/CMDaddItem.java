package commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		
	public class CMDaddItem implements CommandExecutor {
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
			int Count = Main.Main.shp.getInt("Items.Count");
			ArrayList<String> ItemList = new ArrayList<String>();
				if(sender instanceof Player) {
					if(args.length == 1);
					Player p = (Player) sender;
					if(!p.hasPermission("Noneless.addItem")) {
						p.sendMessage("§cDu darfst diesen Befehl nicht Benutzen");
					 return true;
					 
					}else {
					  p.sendMessage("Item wurde Gespeichert");
					  p.getInventory().getItemInMainHand().getType();
					  Main.Main.shp.set(p.getInventory().getItemInMainHand().getType()+".ShopsPreise", args[0]);
					  ItemList.addAll(Main.Main.shp.getStringList("Items.List"));
					  ItemList.add(p.getInventory().getItemInMainHand().getType().toString());
					  Main.Main.shp.set("Items.List",ItemList);
					  Main.Main.shp.set("Items.Count", Count+1);
					  
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
