package commands;





import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Main.Main;
import Main.MySQL;
		





	public class CMDaddFriend implements CommandExecutor  
	{
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			ArrayList<String> Friends = new ArrayList<String>();
			ArrayList<String> p2Friends = new ArrayList<String>();
			ArrayList<String> SendedRequestslist = new ArrayList<String>();
			ArrayList<String> p2BecameRequestslist = new ArrayList<String>();
			ArrayList<String> BecameRequestslist = new ArrayList<String>();
			
			
			
		Player p = (Player) sender;
		

	
		int Count = 0;
		
		if(sender instanceof Player) {
			
			if(Main.Frdb.getString(p.getName()+".Name") == Main.Frdb.getString(args[0]+".Name")){
				p.sendMessage("Du Kannst dich nicht selber adden");
			}else {
			

		
		if(args.length == 1) {
			
			boolean Exists = false;
			boolean First = false;
			
			

			
			
			if(Main.Frdb.getBoolean(p.getName()+".Exists")) {

			}else {
				First = true;
				Main.Frdb.set(p.getName()+".Exists", true);
				Main.Frdb.set(p.getName()+".Count", Count);
				
			}
			
			
			int C2 = Main.Frdb.getInt(p.getName()+".Count");
			if(First == false) {
			Friends.addAll(Main.Frdb.getStringList(p.getName()+".Friends"));
			}
			

				boolean Test = Friends.contains(args[0]);
				
				
				if(Test) { 
					Exists = true; 
					}
			
			
			if(Exists) {
				p.sendMessage("Diesen Freund kannst du nicht mehr adden");
			}else {
				if(C2 == 27) {
					p.sendMessage("Du Kannst nicht mehr Freunde Haben");
				}else {
					if(Main.Frdb.getBoolean(args[0]+".isOnline")) {
						
						
						
						
						
						
						SendedRequestslist.addAll(Main.Frdb.getStringList(p.getName()+".SendetRequests"));
						
						if(SendedRequestslist.contains(args[0])) {
							p.sendMessage("Du hast "+args[0]+" schon eine Anfrage Geschickt");
						}else {
						SendedRequestslist.add(args[0]);
						Main.Frdb.set(p.getName()+".SendetRequests", SendedRequestslist);
						
						p2BecameRequestslist.addAll(Main.Frdb.getStringList(args[0]+".BecameRequests"));
						if(p2BecameRequestslist.contains(p.getName())){
							p.sendMessage("Hier ist ein Fehler unterlaufen Bitte Kontaktiere den Support (FC:AddFriend.already.exists");
						}else {
							BecameRequestslist.addAll(Main.Frdb.getStringList(p.getName()+".BecameRequests"));
							if(BecameRequestslist.contains(args[0])) {
								p.sendMessage("Du Hast Bereits eine Anfrage von "+args[0]+" Erhalten, bitte mit /addfriend accept "+args[0]+" annehmen");
								//Annehmen
							}else {
							p2BecameRequestslist.add(p.getName());
							Main.Frdb.set(args[0]+".BecameRequests", p2BecameRequestslist);
							
							Player p2 = Bukkit.getPlayer(args[0]);
							int BCRCounter = Main.Frdb.getInt(args[0]+".BCRCounter") + 1;
							Main.Frdb.set(args[0]+".BCRCounter",BCRCounter);
							int SDCounter = Main.Frdb.getInt(p.getName()+".SDCounter") +1;
							Main.Frdb.set(p.getName()+".SDCounter", SDCounter);
							p2.sendMessage("Du Hast Eine neue Anfrage von "+p.getName()+" bitte mit /addfriend accept "+p.getName()+" annehmen");
							p.sendMessage("Du hast "+args[0]+" eine Anfrage Geschickt");
						}
							
						
						}
						
						}
				try {
					Main.Frdb.save(Main.Friends);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				}else {
					p.sendMessage("Der Spieler "+args[0]+" ist Leider nicht Online");
				}
				}
			}
			
				
			}else {
				if(args[0].equalsIgnoreCase("accept")) {
				BecameRequestslist.addAll(Main.Frdb.getStringList(p.getName()+".BecameRequests"));
				if(BecameRequestslist.contains(args[1])) {
					

					Player p2 = Bukkit.getPlayer(args[1]);
					
					p.sendMessage("Du Hast eine Anfrage von "+args[1]+" Erhalten, die wird nun angenommen");
					//Annehmen
					int BCRCounter = Main.Frdb.getInt(p.getName()+".BCRCounter") - 1;
					Main.Frdb.set(p.getName()+".BCRCounter",BCRCounter);
					int SDCounter = Main.Frdb.getInt(args[1]+".SDCounter") -1;
					Main.Frdb.set(args[1]+".SDCounter", SDCounter);
					
					
					SendedRequestslist.addAll(Main.Frdb.getStringList(args[1]+".SendetRequests"));
					SendedRequestslist.remove(p.getName());
					Main.Frdb.set(args[1]+".SendetRequests", SendedRequestslist);
					
					
					BecameRequestslist.remove(p2.getName());
					Main.Frdb.set(p.getName()+".BecameRequests", BecameRequestslist);
					
					
					Friends.addAll(Main.Frdb.getStringList(p.getName()+".Friends"));
					Friends.add(p2.getName());
					if(Main.Frdb.getBoolean(p.getName()+".webregister")) {
					try {
						PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO ? (Friend) VALUES (?)");
						ps.setString(1, p.getName()+"_Friends");
						ps.setString(2, p2.getName());
						ps.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}}else {
						p.sendMessage("Benutze /Webregister um den vollenumfang des Servers zu Benutzen");
					}
					
					
					Main.Frdb.set(p.getName()+".Friends", Friends);
					p2Friends.addAll(Main.Frdb.getStringList(args[1]+".Friends"));
					p2Friends.add(p.getName());
					
					if(Main.Frdb.getBoolean(p2.getName()+".webregister")) {
						try {
							PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO ? (Friend) VALUES (?)");
							ps.setString(1, p2.getName()+"_Friends");
							ps.setString(2, p.getName());
							ps.executeUpdate();
						} catch (SQLException e) {
							e.printStackTrace();
						}}else {
							p.sendMessage("Benutze /Webregister um den vollenumfang des Servers zu Benutzen");
						}
					
					
					Main.Frdb.set(args[1]+".Friends", p2Friends);
					int p1Counter = Main.Frdb.getInt(p.getName()+".Count")+1;
					int p2Counter = Main.Frdb.getInt(args[1] + ".Count")+1;
					Main.Frdb.set(p.getName()+".Count", p1Counter);
					Main.Frdb.set(args[1]+".Count", p2Counter);
					p2.sendMessage(p.getName()+" Hat deine Anfrage angenommen");
				
				}else {
					p.sendMessage("Bitte /addfriend accept (Spielername) eingeben");
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