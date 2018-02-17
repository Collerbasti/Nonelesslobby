package commands;





import java.io.IOException;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		





	public class CMDaddFriend implements CommandExecutor {
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			ArrayList<String> Friends = new ArrayList<String>();
			
			
			
		Player p = (Player) sender;
		
		
	
		int Count = 0;
		
		if(sender instanceof Player) {


		
		if(args.length == 1) {
			
			boolean Exists = false;
			boolean First = false;
			
			if(Main.Main.Frdb.getBoolean(p.getName()+".Exists")) {

			}else {
				First = true;
				Main.Main.Frdb.set(p.getName()+".Exists", true);
				Main.Main.Frdb.set(p.getName()+".Count", Count);
				
			}
			
			
			int C2 = Main.Main.Frdb.getInt(p.getName()+".Count");
			if(First == false) {
			Friends.addAll(Main.Main.Frdb.getStringList(p.getName()+".Friends"));
			}
			

				boolean Test = Friends.contains(args[0]);
				p.sendMessage("Rec");
				
				if(Test) { 
					Exists = true; 
					}
			
			
			if(Exists) {
				p.sendMessage("Diesen Freund kannst du nicht mehr adden");
			}else {
				if(C2 == 27) {
					p.sendMessage("Du Kannst nicht mehr Freunde Haben");
				}else {
					if(Main.Main.Frdb.getBoolean(args[0]+".isOnline")) {
				Friends.add(args[0]);
				Main.Main.Frdb.set(p.getName()+"Friends", Friends);
				Main.Main.Frdb.set(p.getName()+".Count", C2+1);
				try {
					Main.Main.Frdb.save(Main.Main.Friends);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
				}
				}else {
					p.sendMessage("Der Spieler "+args[0]+" ist Leider nicht Online");
				}
				}
			}
				
			}
		
			
			
			
		
			  
			}else {
				return false;
			}
				
		
		
			
			
		
		
		
		return true;
		}
		
	}