package de.noneless;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class MySQL {
    private static final Logger LOGGER = Logger.getLogger(MySQL.class.getName());
	
	public static String host = "ms3693.gamedata.io";
	public static String port = "3306";
	public static String database = "ni506153_1_DB";
	public static String username = "ni506153_1_DB";
	public static String password = "4sJ3FY3F";
	public static String database2 = "ni506153_1_DB";
	public static String username2 = "ni506153_1_DB";
	public static String password2 = "4sJ3FY3F";
	public static Connection con;
	public static Connection con2;
	
	private static final String CONNECTION_URL = "jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=false&maxReconnects=10&initialTimeout=10&interactiveClient=true&tcpKeepAlive=true";
	
    public static void connect2() {
    	if(!isConnected()) {
    		System.out.println("[MySQL] Verbindung wird versucht aufzubauen!");
    		try {
				String url = String.format(CONNECTION_URL, host, port, database);
				con = DriverManager.getConnection(url, username, password);
				System.out.println("[MySQL] Verbindung aufgebaut!");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MySQL] Verbindung fehlgeschlagen");
			}
    	}
    }
    
    public static void connect() {
        if (!isConnected()) {
            LOGGER.info("[MySQL] Verbindung wird versucht aufzubauen!");
            try {
                String url = String.format(CONNECTION_URL, host, port, database);
                con = DriverManager.getConnection(url, username, password);
                LOGGER.info("[MySQL] Verbindung aufgebaut!");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "[MySQL] Verbindung fehlgeschlagen", e);
            }
            try (PreparedStatement ps = MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS PUNKTESYSTEM (UUID VARCHAR(100), SPIELERNAME VARCHAR(100), PUNKTE INT(100))")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[MySQL] Fehler beim Erstellen der Tabelle PUNKTESYSTEM", e);
            }
            try (PreparedStatement ps = MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS PROFILELIST (NICKNAME VARCHAR(100), SPIELERNAME VARCHAR(100), PASSWORT VARCHAR(100), WEITERES VARCHAR(100))")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "[MySQL] Fehler beim Erstellen der Tabelle PROFILELIST", e);
            }
        } else {
            LOGGER.warning("[MySQL] Verbindung fehlgeschlagen 2");
        }
    }
    
    public static void disconnect() {
    	if(isConnected()) {
    		try {
				con.close();
				System.out.println("[MySQL] Verbindung geschlossen!");
				con = null;
			} catch (SQLException e) {
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
				e.printStackTrace();
			}
    	 }
    }
    
    public static boolean isConnected() {
    	try {
    		return con != null && !con.isClosed();
    	} catch (SQLException e) {
    		return false;
    	}
    }
    
    public static boolean isConnected2() {
    	try {
    		return con2 != null && !con2.isClosed();
    	} catch (SQLException e) {
    		return false;
    	}
    }
    
    public static Connection getConnection() {
    	if (!isConnected()) {
    		connect();
    	}
    	return con;
    }
    
    public static Connection getConnection2() {
    	if (!isConnected2()) {
    		connect2();
    	}
    	return con2;
    }

    public Bukkit getServer() {
        return null;
    }
}





