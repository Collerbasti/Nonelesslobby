package commands;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		
	public class CMDsetlobby implements CommandExecutor {

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(!p.hasPermission("Noneless.lobby.set")) {
						p.sendMessage("§cDu darfst diesen Befehl nicht Benutzen");
					 return true;
					}
					if(args.length == 0) {
					p.sendMessage("Lobby Wird Gespeichert");
					Location loc = p.getLocation();
					Main.Main.loc.set("spawn.X", loc.getX());
					Main.Main.loc.set("spawn.Y", loc.getY());
					Main.Main.loc.set("spawn.Z", loc.getZ());
					Main.Main.loc.set("spawn.Yaw", loc.getYaw());
					Main.Main.loc.set("spawn.Pitch", loc.getPitch());
					Main.Main.loc.set("spawn.World", loc.getWorld().getName());
					try {
					Main.Main.loc.save(Main.Main.Locations);
				}catch (IOException e) {
					e.printStackTrace();
					
				}
				}else if(args.length == 1) {
					
					
					p.sendMessage( args[0] +" Wird Gespeichert");
					Location loc = p.getLocation();
					Main.Main.loc.set(args[0]+".true", "true");
					Main.Main.loc.set(args[0]+".X", loc.getX());
					Main.Main.loc.set(args[0]+".Y", loc.getY());
					Main.Main.loc.set(args[0]+".Z", loc.getZ());
					Main.Main.loc.set(args[0]+".Yaw", loc.getYaw());
					Main.Main.loc.set(args[0]+".Pitch", loc.getPitch());
					Main.Main.loc.set(args[0]+".World", loc.getWorld().getName());
					try {
					Main.Main.loc.save(Main.Main.Locations);
				}catch (IOException e) {
					e.printStackTrace();
					
				}
					
					return true;
					
				}else {
					
					return true;
				}
					
			
		}
				return true;
			}

	
	
}
