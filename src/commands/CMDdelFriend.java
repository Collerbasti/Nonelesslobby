package commands;





import java.io.IOException;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		




 
	public class CMDdelFriend implements CommandExecutor  
	{
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
			Player p = (Player) sender;
			
			if(sender instanceof Player) {
				ArrayList<String> Friends = new ArrayList<String>();
				if(args.length==1) {
					
					Friends.addAll(Main.Main.Frdb.getStringList(p.getName()+".Friends"));
					if(Friends.contains(args[0])){
						int Counter = Main.Main.Frdb.getInt(p.getName()+".Count");
								Friends.remove(args[0]);
							Main.Main.Frdb.set(p.getName()+".Friends", Friends);
							Main.Main.Frdb.set(p.getName()+".Count", Counter-1);
							p.sendMessage("Du bist nicht mehr mit "+args[0]+" befreundet" );
							try {
								Main.Main.Frdb.save(Main.Main.Friends);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
					}else {
						p.sendMessage("Du bist nicht mit "+args[0]+" befreundet" );
					}
					
					
					
					
					
					
				}else {
					p.sendMessage("§4/delfriend (freund)");
				}
			}
			
			return true;
			
		}
}
