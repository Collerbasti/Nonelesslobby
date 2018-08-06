package commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Main.Main;
		
	public class CMDremoveKIcommand implements CommandExecutor {

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args)  
		{
			
			if(sender instanceof Player) {
				if(sender.hasPermission("Noneless.GlaDOS")) { 
				Player p = ((Player) sender).getPlayer();
				ArrayList<String> Glados = new ArrayList<String>();
				Glados.addAll(Main.GDOS.getStringList("GlaDOS.List"));
				if(Glados.contains(args[0])) {
					
					if(Glados.remove(args.toString())) {
						p.sendMessage(args.toString()+" wurde erfolgreich gelöscht");
						Main.GDOS.set("GlaDOS.List", Glados);
						p.sendMessage(Main.GDOS.get("GlaDOS."+args.toString()+".Player")+" war es übrigens");
						Main.GDOS.set("GlaDOS."+args[0]+".answer",null);
						Main.GDOS.set("GlaDOS."+args[0]+".Player",null);
					}}
						
				}
				
				
				
				
				
				return true;
			}else {
				return false;
			}
			
			
		}
			

}
//removeKIcommand (KICommand)