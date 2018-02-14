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
				}
			
				return true;
		}

	
	
}
