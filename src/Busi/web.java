package Busi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import Main.MySQL;

public class web {

	public static String host = "localhost";
	public static String port = "3306";
	public static String database = "BusyUser";
	public static String username = "BusyUser";
	public static String password = "rc7u00w5Htdq2afj";
	public static Connection con;

    public static void connect() {
    	if(!isConnected()) {
    		System.out.println("[MySQL][Busy] Verbindung wird versucht aufzubauen!");
    		try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
				System.out.println("[MySQL] Verbindung aufgebaut!");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("[MySQL][Busy] Verbindung fehlgeschlagen");
			}
    		
    		
    	}else {
    		System.out.println("[MySQL][Busy] Verbindung fehlgeschlagen");
    	}
    }
    
    public static void disconnect() {
    	if(isConnected()) {
    		try {
				con.close();
				System.out.println("[MySQL][Busy] Verbindung geschlossen!");
				con = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
    }
  
    
    public static boolean isConnected() {
		return (con == null ? false : true);
    	
    }
    
    
    public static  Connection getConnection() {
  	  return con;
    }


public Bukkit getServer() {
	// TODO Auto-generated method stub
	return null;
}


	public static Boolean listallquest(String KiName) {
		Bukkit.broadcastMessage("§4"+KiName+": §f online: ");
		try {
			String pss = "SELECT * FROM Requests WHERE 1";
		PreparedStatement ps = Busi.web.getConnection().prepareStatement(pss);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			
				
				Bukkit.broadcastMessage(rs.getString("Quest")+"!?");
		}
		
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static String Answere(String Request) {
		String Answere = null;
		String pss = null;
		Request = Request.replace("!?", "");
		if(Request != null || Request != "")
		{
			try {
				pss = "SELECT * FROM Requests WHERE `Quest` LIKE \""+Request+"\"";
			PreparedStatement ps = Busi.web.getConnection().prepareStatement(pss);
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
