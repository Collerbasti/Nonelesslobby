package de.noneless.Busi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import de.noneless.*;

public class web {
    private static final Logger LOGGER = Logger.getLogger(web.class.getName());
    public static String host = "localhost";
    public static String port = "3306";
    public static String database = "BusyUser";
    public static String username = "BusyUser";
    public static String password = "rc7u00w5Htdq2afj";
    public static Connection con;

    public static void connect() {
        if (!isConnected()) {
            LOGGER.info("[MySQL][Busy] Verbindung wird versucht aufzubauen!");
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                LOGGER.info("[MySQL] Verbindung aufgebaut!");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "[MySQL][Busy] Verbindung fehlgeschlagen", e);
            }
        } else {
            LOGGER.warning("[MySQL][Busy] Verbindung fehlgeschlagen (bereits verbunden)");
        }
    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                con.close();
                LOGGER.info("[MySQL][Busy] Verbindung geschlossen!");
                con = null;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "[MySQL][Busy] Fehler beim Schließen der Verbindung", e);
            }
        }
    }

    public static boolean isConnected() {
        return con != null;
    }

    public static Connection getConnection() {
        return con;
    }

    public Bukkit getServer() {
        return null;
    }

    public static Boolean listallquest(String KiName) {
        Bukkit.broadcastMessage("§4" + KiName + ": §f online: ");
        String sql = "SELECT * FROM Requests WHERE 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Bukkit.broadcastMessage(rs.getString("Quest") + "!?");
            }
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Fehler beim Abrufen der Quests", e);
            return false;
        }
    }
    
    public static String Answere(String Request) {
		String Answere = null;
		String pss = null;
		Request = Request.replace("!?", "");
		if(Request != null || Request != "")
		{
			try {
				pss = "SELECT * FROM Requests WHERE `Quest` LIKE \""+Request+"\"";
			PreparedStatement ps = de.noneless.Busi.web.getConnection().prepareStatement(pss);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				
					Answere = rs.getString("Answere");
				
			}
			
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(Answere ==null||Answere == "") {
			Answere = "Ich weis leider keine Anstwort darauf : "+pss;
		}
	return Answere;
	}
}
