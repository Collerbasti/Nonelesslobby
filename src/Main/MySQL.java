package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;

public class MySQL {
	
	public static String host = "localhost";
	public static String port = "3306";
	public static String database = "RangSystem";
	public static String username = "RangSystem";
	public static String password = "GGHDFHHGFDSL45SDA";
	public static String database2 = "Minecraft_Icon";
	public static String username2 = "Minecraft_Icon";
	public static String password2 = "DmwApZJ7slt86aLO";
	public static Connection con;
	public static Connection con2;
	
	
	
	
    public static void connect2() {
    	if(!isConnected()) {
    		System.out.println("[MySQL] Verbindung wird versucht aufzubauen!");
    		try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
				System.out.println("[MySQL] Verbindung aufgebaut!");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("[MySQL] Verbindung fehlgeschlagen");
			}
    	}
    }
    public static void connect() {
    	if(!isConnected()) {
    		System.out.println("[MySQL] Verbindung wird versucht aufzubauen!");
    		try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
				System.out.println("[MySQL] Verbindung aufgebaut!");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("[MySQL] Verbindung fehlgeschlagen");
			}
    		
    		try {
    			PreparedStatement ps = MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS PUNKTESYSTEM (UUID VARCHAR(100), SPIELERNAME VARCHAR(100), PUNKTE INT(100))");
    			ps.executeUpdate();
        	} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		try {
    			PreparedStatement ps = MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS PROFILELIST (NICKNAME VARCHAR(100), SPIELERNAME VARCHAR(100), PASSWORT VARCHAR(100), WEITERES VARCHAR(100))");
    			ps.executeUpdate();
        	} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}else {
    		System.out.println("[MySQL] Verbindung fehlgeschlagen 2");
    	}
    }
    
    public static void disconnect() {
    	if(isConnected()) {
    		try {
				con.close();
				System.out.println("[MySQL] Verbindung geschlossen!");
				con = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
    }
    public static void disconnect2() {
    	if(isConnected2()) {
    		try {
				con2.close();
				System.out.println("[MySQL] Verbindung geschlossen!");
				con2 = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
    }
    
    public static boolean isConnected() {
		return (con == null ? false : true);
    	
    }
    public static boolean isConnected2() {
		return (con2 == null ? false : true);
    	
    }
    
    public static  Connection getConnection() {
  	  return con;
    }
    public static  Connection getConnection2() {
  	  return con2;
    }

public Bukkit getServer() {
	// TODO Auto-generated method stub
	return null;
}
}
  
    



