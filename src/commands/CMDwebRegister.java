package commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Main.Main;
import Main.MySQL;

public class CMDwebRegister implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(sender instanceof Player) {
			Player p = (Player) sender;
		//register Nickname Password Password
		if(MySQL.isConnected()) {
			if(isUserExists(p.getUniqueId(), p)) {
				p.sendMessage("du bist bereits im System, wenn du dein passwort vergessen hast melde dich bei einem Supporter");
			}
			else {
				if(args.length == 3) {
					
					String PW =  args[1];
					
					String PW2 =  args[2];
					if(PW.contains(args[2])) {
						
						if(!isNickExists(args[0], p)) {
							try {
								PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO PROFILELIST (UUID,NICKNAME,SPIELERNAME,PASSWORT) VALUES (?,?,?,?)");
								ps.setString(1, p.getUniqueId().toString());
								ps.setString(2, args[0]);
								ps.setString(3, p.getName());
								ps.setString(4, args[1]);
								p.sendMessage("Jetzt kannst du dich anmelden online");
								Main.Frdb.set(p.getName()+".webregister", true);
								
								
								
								
								ps.executeUpdate();
							} catch (SQLException e) {
								p.sendMessage("sorry aber irgentwas ist schiefgelaufen ( "+ e+" )");
								e.printStackTrace();
							}
							
							try {
								String psSt = "CREATE TABLE IF NOT EXISTS "+p.getName()+"_Friends (Friend TEXT(100))";
				    			PreparedStatement ps2 = MySQL.getConnection().prepareStatement(psSt);
				    			
				    			ps2.executeUpdate();
				    			
				    			
				    			int Counter = Main.Frdb.getInt(p.getName()+".Count");
				    			ArrayList<String> Friends = new ArrayList<String>();
				    			Friends.addAll(Main.Frdb.getStringList(p.getName()+".Friends"));
				    			while(Counter > 0) {
				    			Counter = Counter -1;
				    			
				    			
				    			try {
				    				PreparedStatement ps3 = MySQL.getConnection().prepareStatement("INSERT INTO "+p.getName()+"_Friends (Friend) VALUES (?)");
				    			 ps3.setString(1, Friends.get(Counter));
				    				ps3.executeUpdate();
				    			}catch(SQLException e) {
				    				p.sendMessage(" "+e);
				    				e.printStackTrace();
				    			}
				    			
				    			
				    			
				    			
				    			}
				        	} catch (SQLException e) {
				        		p.sendMessage(" "+e);
				    			// TODO Auto-generated catch block
				    			e.printStackTrace();
				    		
				        	}
							
							
							
						}else {
							p.sendMessage("Bitte anderen nutzernamen verwenden");
						}
						
						
						
					}else {
						p.sendMessage("Deine Passwörter stimmen nicht überein!!!"+PW+PW2);
					}
					
					
				}else {
					p.sendMessage("Bitte gebe folgendes an: register Nickname Passwort Passwortwdhl");
				}
			}
			
			
		}else {
			p.sendMessage("es tut uns leid aber irgentwas ist schief gelaufen, bitte melde dies einem Supporter");
		}
		
		
		
		
		
		
		}else {
			return false;
		}
		
		
		
		
		return false;
	}

	
	  public static boolean isUserExists(UUID uuid, Player p) {
			
			PreparedStatement ps;
			try {
				ps = MySQL.getConnection().prepareStatement("SELECT WEITERES FROM PROFILELIST WHERE UUID = ?");
				ps.setString(1, uuid.toString());
				ResultSet rs = ps.executeQuery();
				return rs.next();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		return false;
		}
	  
	  public static boolean isNickExists(String Nick, Player p) {
			
			PreparedStatement ps;
			try {
				ps = MySQL.getConnection().prepareStatement("SELECT * FROM `PROFILELIST` WHERE `NICKNAME` LIKE ?");
				ps.setString(1, Nick);
				p.sendMessage(Nick);
				ResultSet rs = ps.executeQuery();
				return rs.next();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		return false;
		}
}
