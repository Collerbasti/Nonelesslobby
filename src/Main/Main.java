package Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import commands.CMDHilfe;
import commands.CMDMagic;
import commands.CMDMySQLConnect;
import commands.CMDMySQLdisConnect;
import commands.CMDaddFriend;
import commands.CMDaddGame;
import commands.CMDaddItem;
import commands.CMDaddVIP;
import commands.CMDdelFriend;
import commands.CMDitemList;
import commands.CMDremoveKIcommand;
import commands.CMDreport;
import commands.CMDsetRang;
import commands.CMDsetlobby;
import commands.CMDspawn;
import commands.CMDvote;
import commands.CMDwebRegister;


public class Main extends JavaPlugin implements Listener  
 {
	
	public static File Locations;
	public static FileConfiguration loc;
	public static File Friends;
	public static FileConfiguration Frdb;
	public static File ShopsPreise;
	public static FileConfiguration shp;
	public static File Reports;
	public static FileConfiguration rpt;
	public static File Minigames;
	public static FileConfiguration MiGm;
	public static File GlaDOS;
	public static FileConfiguration GDOS;	
	public static File AdminOnline;
	public static FileConfiguration AOnline;	
	public int Timer = 0;
	public int Timer2 = 0;

	
	@Override	

	public void onEnable() {
         //Test 1234
		MySQL.connect();
		StartTimer();
		ReconnectData();

		Bukkit.getPluginManager().registerEvents((Listener) this, this);

		new NonelessEventListener(this);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		this.getCommand("disconnect").setExecutor(new CMDMySQLdisConnect());
		this.getCommand("connect").setExecutor(new CMDMySQLConnect());
		this.getCommand("vote").setExecutor(new CMDvote());
		this.getCommand("spawn").setExecutor(new CMDspawn());
		this.getCommand("setlobby").setExecutor(new CMDsetlobby());
		this.getCommand("addFriend").setExecutor(new CMDaddFriend());
		this.getCommand("delFriend").setExecutor(new CMDdelFriend());
		this.getCommand("addItem").setExecutor(new CMDaddItem());
		this.getCommand("itemList").setExecutor(new CMDitemList());
		this.getCommand("report").setExecutor(new CMDreport());
		this.getCommand("addGame").setExecutor(new CMDaddGame());
		this.getCommand("setRang").setExecutor(new CMDsetRang());
		this.getCommand("removeKIcommand").setExecutor(new CMDremoveKIcommand());
		this.getCommand("Magic").setExecutor(new CMDMagic());
		this.getCommand("AddVip").setExecutor(new CMDaddVIP()); 
		this.getCommand("webRegister").setExecutor(new CMDwebRegister()); 
		this.getCommand("Hilfe").setExecutor(new CMDHilfe()); 
		
    	//Setupfiles Erzeugen
		
    	Main.Locations = new File("plugins/Noneless","Warps.yml");
    	Main.loc = YamlConfiguration.loadConfiguration(Main.Locations);
    	
    	Main.Friends = new File("plugins/Noneless","FriendsDB.yml");
    	Main.Frdb = YamlConfiguration.loadConfiguration(Main.Friends); 
    	
    	
    	Main.ShopsPreise = new File("plugins/Noneless","ShopItems.yml");
    	Main.shp = YamlConfiguration.loadConfiguration(Main.ShopsPreise);
    	
    	Main.Reports = new File("plugins/Noneless","Reports.yml");
    	Main.rpt = YamlConfiguration.loadConfiguration(Main.Reports);
    	
    	Main.Minigames = new File("plugins/Noneless","MGames.yml");
    	Main.MiGm = YamlConfiguration.loadConfiguration(Main.Minigames);

    	Main.GlaDOS = new File("plugins/Noneless","GlaDOS.yml");
    	Main.GDOS = YamlConfiguration.loadConfiguration(Main.GlaDOS);

    	Main.AdminOnline = new File("plugins/Noneless","AOnline.yml");
    	Main.AOnline = YamlConfiguration.loadConfiguration(Main.AdminOnline);
    	
    	System.out.println("Das Plugin wurde aktiviert!");	

    	


    	 }

	
	
	public void teleportToServer(Player player, String server) {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    DataOutputStream out = new DataOutputStream(b);
	    try {
	        out.writeUTF("Connect");
	        out.writeUTF(server);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}

	
	
	private void ReconnectData() {
		
    	
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
    		@Override
    	public void run() {
    		Timer =Timer + 1;
    		if(Timer == 60) {
    			MySQL.disconnect();
    		}else if(Timer == 61) {
    			MySQL.connect();
    			Timer = 0;
    		}
    		}
    		},20*60, 20*60);
    	}
    			
    private void StartTimer() {
		
    	
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
    		@Override
    	public void run() {
    			Timer2 =Timer2+1;
    			
    			boolean EnderDragon = false;
    			if (Timer2==3) {
    			Timer2 =0;

				
    			
    				
    					for(Entity ent : Bukkit.getWorld("starwars").getEntities()) {
    						if(ent.getType()==EntityType.ENDER_DRAGON) {
    							EnderDragon = true;
    							
    						}
    					}
    					if(EnderDragon) {
    						double x = 0;
    						double y = 80;
    						double z = 0;
    						float yaw = 0;
    						float pitch = 0;
    						String World2 ="starwars";
    						LivingEntity entity = (LivingEntity) Bukkit.getWorld(World2).spawnEntity(new Location(Bukkit.getWorld(World2), x, y, z, yaw, pitch), EntityType.CREEPER);
    						entity.setCustomName("The NoneDragon");
    						
    						EnderDragon =  false;
    					
    					
    				
    			}
    			}
    			
    			
    			
    			
    			
    			for(Player players : Bukkit.getOnlinePlayers()) {
    				
    				if(players.hasPermission("Noneless.Admin")) {
    					Date date = new Date();
    					Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
    					calendar.setTime(date);   // assigns calendar to given date 
    					int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
    				
    					int Counter = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour))+1;
    					Main.AOnline.set(players.getName()+"."+Integer.toString(hour), Counter);
    					
    					
    					try {
							Main.AOnline.save(Main.AdminOnline);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    				}
    				
    				
    				
    				
    				
    				if(EnderDragon) {
    				players.playSound(players.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
    				}
    				int Min = Frdb.getInt(players.getName() + ".Online.Min");
    				int Std = Frdb.getInt(players.getName() + ".Online.Std");
    						Main.GDOS.set(players.getName().toString()+".EasyMode.Magic",Main.GDOS.getInt(players.getName().toString()+".EasyMode.Magic")+1);
    				if(Min == 60) {
    					Min = 0;
    					Std = Std+1;
    					players.sendMessage("Du bist nun insgesammt : "+Std+" Stunden auf dem Server, dafür bekommst du einen Punkt");
    					Mysql.Punkte.Update(players.getUniqueId(), 1, players.getName(), false , players);
    				}else {
    					Min = Min+1;
    					
    					for(Player on:Bukkit.getServer().getOnlinePlayers()){
    						if(Frdb.getBoolean(on.getName()+".webregister")) {
    						try {
								PreparedStatement ps3 = MySQL.getConnection().prepareStatement("SELECT * FROM "+on.getName()+"_Friends");
								ResultSet rs = ps3.executeQuery();
								
								while(rs.next()) {
									if(rs.getString("MESSAGE")!="") {
										if(rs.getString("ZUGESTELLT")=="") {
										on.sendMessage("Du hast eine Nachricht von: "+rs.getString("Friend"));
										on.sendMessage(rs.getString("MESSAGE"));
										MySQL.getConnection().prepareStatement("UPDATE "+on.getName()+"_Friends SET `ZUGESTELLT`=\"ja\" WHERE Friend LIKE \""+rs.getString("Friend")+"\"").executeUpdate();
										
										
										}
									}
								}
								
								
								
								
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								on.sendMessage(" "+e);
							}
			    			
    						
    							
    						
    			     
    			        }
    					
    					
    					}
    					
    				}
    				Frdb.set(players.getName() + ".Online.Min", Min);
    				Frdb.set(players.getName() + ".Online.Std", Std);
    			}
    			}
    	}, 20*60, 20*60);
    		
    	
    	
    	}
    
    
    public void StartTimer2() {
    
    Bukkit.getScheduler().scheduleSyncRepeatingTask( this, new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}, 20*60, 20*60);
	
    }
	public void onDisable() {
    	MySQL.disconnect();
        for(Player on:Bukkit.getServer().getOnlinePlayers()){
           NonelessEventListener.TeleporttoServer(on, "survival_1");
        	Main.Frdb.set(on.getName()+".isOnline", false);
     
        }
        try {
			Speak_Class.Speak("Der Mienecraft Server Startet einmal Neu");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        try {
			GDOS.save(GlaDOS);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       	try {
    				Main.Frdb.save(Main.Friends);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
}



	
	}



 //Hallo internet , HeY WIe GeHtS