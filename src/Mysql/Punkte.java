package Mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.entity.Player;

public class Punkte {
	
	public static void Update(UUID uuid , int amount , String Playername ,boolean remove , Player p) {
		
		
		
		
		if(remove) {

if(isUserExists(uuid,p)) {
				
				try {
					PreparedStatement ps = Main.MySQL.getConnection().prepareStatement("UPDATE PUNKTESYSTEM SET Punkte = ? WHERE UUID = ?");
					ps.setString(2, uuid.toString());
					
					ps.setInt(1, getPoints(uuid) + amount);
					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				
				
				
			}else {
				 
				try {
					PreparedStatement ps = Main.MySQL.getConnection().prepareStatement("INSERT INTO PUNKTESYSTEM (UUID,SPIELERNAME,PUNKTE) VALUES (?,?,?)");
					ps.setString(1, uuid.toString());
					ps.setString(2, Playername);
					ps.setInt(3, amount);
					ps.executeUpdate();
				} catch (SQLException e) {	
					e.printStackTrace();
				}
				
			}
			
			
		}else {
			
			if(isUserExists(uuid,p)) {
				
				try {
					PreparedStatement ps = Main.MySQL.getConnection().prepareStatement("UPDATE PUNKTESYSTEM SET Punkte = ? WHERE UUID = ?");
					ps.setString(2, uuid.toString());
					
					ps.setInt(1, getPoints(uuid) + amount);
					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				
				
				
			}else {
				 
				try {
					PreparedStatement ps = Main.MySQL.getConnection().prepareStatement("INSERT INTO PUNKTESYSTEM (UUID,SPIELERNAME,PUNKTE) VALUES (?,?,?)");
					ps.setString(1, uuid.toString());
					ps.setString(2, Playername);
					ps.setInt(3, amount);
					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
			
			
		}
		
	}

	
	public static boolean isUserExists(UUID uuid, Player p) {
		p.sendMessage("Danten Test");
		PreparedStatement ps;
		try {
			ps = Main.MySQL.getConnection().prepareStatement("SELECT Punkte FROM PUNKTESYSTEM WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	return false;
	}
	
	public static Integer getPoints(UUID uuid) {
		
		try {
			PreparedStatement ps = Main.MySQL.getConnection().prepareStatement("SELECT Punkte FROM PUNKTESYSTEM WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				return rs.getInt("Punkte");
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
		
	}
	
	
}
