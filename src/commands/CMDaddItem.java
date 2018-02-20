package commands;

import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		
	public class CMDaddItem implements CommandExecutor {
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

				if(sender instanceof Player) {
					if(args.length == 1);
					Player p = (Player) sender;
					if(command.getName().equals("addItem"));
					if(!p.hasPermission("Noneless.addItem")) {
						p.sendMessage("§cDu darfst diesen Befehl nicht Benutzen");
					 return true;
					 
					}else {
					  p.getInventory().getItemInMainHand().getType();
					  Main.Main.shp.set(p.getInventory().getItemInMainHand().getType()+".ShopsPreise", args[0]);
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
