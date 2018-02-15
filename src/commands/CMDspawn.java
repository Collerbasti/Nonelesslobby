package commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Main.Main;

public class CMDspawn implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
	
	
		if(command.getName().equals("spawn")) {
			if(args.length == 0) {
				if(sender instanceof Player) {
				
				
					Double x = Main.loc.getDouble("spawn.X");
					Double y = Main.loc.getDouble("spawn.Y");
					Double z = Main.loc.getDouble("spawn.Z");
					Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
					Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
					org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
					p.teleport(new Location(w,x,y,z,yaw,pitch));
			}

				
			
	}else if(args.length == 1) {
		String Truetest = Main.loc.getString(args[0]+".true");
		if(Truetest == null) {
		p.sendMessage(args[0]+" Existiert nicht");
		}else {
			
			if(sender instanceof Player) {
				
				
				Double x = Main.loc.getDouble(args[0]+".X");
				Double y = Main.loc.getDouble(args[0]+".Y");
				Double z = Main.loc.getDouble(args[0]+".Z");
				Float yaw = (float) Main.loc.getDouble(args[0]+".Yaw");
				Float pitch = (float) Main.loc.getDouble(args[0]+".Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString(args[0]+".World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
		}
		}
		}
		
		
	}
			return false;
	}

}