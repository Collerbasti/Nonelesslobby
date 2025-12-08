package de.noneless;

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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.NamespacedKey;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.noneless.commands.*;


import de.noneless.Menues.*;


public class Main extends JavaPlugin implements Listener  
 {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
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
	public int hourlyPointsTimer = 0; // Timer für die stündliche Punktevergabe

    public static class Menues {
        public static Warps Warps;
        public static Settings Settings;
        public static Freunde Freunde;
        public static Games Games;
    }
	
	@Override	

	public void onEnable() {
        try {
            MySQL.connect();
            StartTimer();
            ReconnectData();
            Bukkit.getPluginManager().registerEvents((Listener) this, this);
            new NonelessEventListener(this);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            // Initialize menus
            Menues.Warps = new Warps();
            Menues.Settings = new Settings();
            Menues.Freunde = new Freunde();
            Menues.Games = new Games();
            LOGGER.info("Plugin erfolgreich aktiviert.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Initialisieren des Plugins", e);
        }
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
        final Main plugin = this;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Timer2 = Timer2 + 1;
                if (Timer2 == 3) {
                    Timer2 = 0;
                    World starwarsWorld = Bukkit.getWorld("starwars");
                    if (starwarsWorld != null) {
                        boolean enderDragonExists = starwarsWorld.getEntities().stream()
                            .anyMatch(entity -> entity.getType() == EntityType.ENDER_DRAGON);
                        if (enderDragonExists) {
                            Location spawnLoc = new Location(starwarsWorld, 0, 80, 0, 0, 0);
                            LivingEntity entity = (LivingEntity) starwarsWorld.spawnEntity(spawnLoc, EntityType.CREEPER);
                            entity.setCustomName("The NoneDragon");
                            entity.setCustomNameVisible(true);
                            PersistentDataContainer container = entity.getPersistentDataContainer();
                            NamespacedKey key = new NamespacedKey(plugin, "custom_entity");
                            container.set(key, PersistentDataType.STRING, "nonedragon");
                        }
                    }
                }
            }
        }, 20 * 60, 20 * 60);

        // Asynchrone stündliche Punktevergabe
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    int punkteSpieler = 0;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player != null && player.isOnline()) {
                            try {
                                int punkte = 10 + (int)(Math.random() * 21); // 10 bis 30
                                if (Math.random() < 0.01) { // 1% Chance auf 100 Punkte
                                    punkte = 100;
                                }
                                de.noneless.Mysql.Punkte.Update(
                                    player.getUniqueId(),
                                    punkte,
                                    player.getName(),
                                    false,
                                    player
                                );
                                player.sendMessage("§6Du hast " + punkte + " Punkte für eine Stunde Online-Zeit erhalten!");
                                punkteSpieler++;
                            } catch (Exception ex) {
                                Bukkit.getLogger().warning("Fehler bei Punktevergabe für " + player.getName() + ": " + ex.getMessage());
                            }
                        }
                    }
                    if (punkteSpieler > 0) {
                        Bukkit.broadcastMessage("§a" + punkteSpieler + " Spieler haben gerade Punkte für ihre Online-Zeit erhalten!");
                    }
                });
            }
        }, 20 * 60 * 60, 20 * 60 * 60); // Läuft jede Stunde (20 Ticks * 60 Sekunden * 60 Minuten)
    }

	public void onDisable() {
    	MySQL.disconnect();
        for(Player on:Bukkit.getServer().getOnlinePlayers()){
           //NonelessEventListener.TeleporttoServer(on, "survival_1");
        	Main.Frdb.set(on.getName()+".isOnline", false);
        }
        Speak_Class.Speak("Der Mienecraft Server Startet einmal Neu");
        // Sicherstellen, dass GDOS initialisiert ist
        if (GDOS != null && GlaDOS != null) {
            try {
                GDOS.save(GlaDOS);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            Main.Frdb.save(Main.Friends);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



	
	}



 //Hallo internet , HeY WIe GeHtS
