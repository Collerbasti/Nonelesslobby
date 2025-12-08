package de.noneless.Mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public class Punkte {
    private static final Logger LOGGER = Logger.getLogger(Punkte.class.getName());
	
	public static void Update(UUID uuid , int amount , String Playername ,boolean remove , Player p) {
		
		
		
		
		if(remove) { 

if(isUserExists(uuid,p)) {
				
				try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("UPDATE PUNKTESYSTEM SET Punkte = ? WHERE UUID = ?")) {
					ps.setString(2, uuid.toString());
					
					ps.setInt(1, getPoints(uuid) - amount);
					ps.executeUpdate();
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Fehler beim Punkte-Abzug", e);
				}
				
				
				
				
			}else {
				 
				try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("INSERT INTO PUNKTESYSTEM (UUID,SPIELERNAME,PUNKTE) VALUES (?,?,?)")) {
					ps.setString(1, uuid.toString());
					ps.setString(2, Playername);
					ps.setInt(3, amount);
					ps.executeUpdate();
				} catch (SQLException e) {	
					LOGGER.log(Level.WARNING, "Fehler beim Einfügen neuer Punkte (Abzug)", e);
				}
				
			}
			
			
		}else {
			
			if(isUserExists(uuid,p)) {
				
				try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("UPDATE PUNKTESYSTEM SET Punkte = ? WHERE UUID = ?")) {
					ps.setString(2, uuid.toString());
					
					ps.setInt(1, getPoints(uuid) + amount);
					ps.executeUpdate();
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Fehler beim Punkte-Hinzufügen", e);
				}
				
				
				
				
			}else {
				 
				try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("INSERT INTO PUNKTESYSTEM (UUID,SPIELERNAME,PUNKTE) VALUES (?,?,?)")) {
					ps.setString(1, uuid.toString());
					ps.setString(2, Playername);
					ps.setInt(3, amount);
					ps.executeUpdate();
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Fehler beim Einfügen neuer Punkte (Hinzufügen)", e);
				}
				
			}
			
			
			
		}
		
	}

	
	public static boolean isUserExists(UUID uuid, Player p) {
		
		try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("SELECT Punkte FROM PUNKTESYSTEM WHERE UUID = ?")) {
			ps.setString(1, uuid.toString());
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	return false;
	}
	
	public static Integer getPoints(UUID uuid) {
		
		try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("SELECT Punkte FROM PUNKTESYSTEM WHERE UUID = ?")) {
			ps.setString(1, uuid.toString());
			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					return rs.getInt("Punkte");
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
		
	}
	
	
}
