package Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.io.IOException;



import commands.CMDaddFriend;
import commands.CMDaddGame;
import commands.CMDaddItem;
import commands.CMDdelFriend;
import commands.CMDitemList;
import commands.CMDreport;
import commands.CMDsetRang;
import commands.CMDsetlobby;
import commands.CMDspawn;
import commands.CMDvote;


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
	

	
	@Override	

	public void onEnable() {
        
		MySQL.connect();
		StartTimer();
	

		Bukkit.getPluginManager().registerEvents(this, this);

		new NonelessEventListener(this);
		
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
    	
    	
    	
    	System.out.println("Das Plugin wurde aktiviert!");	


    	 }
	
    private void StartTimer() {
		
    	
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
    		@Override
    	public void run() {
    			for(Player players : Bukkit.getOnlinePlayers()) {
    				
    				int Min = Frdb.getInt(players.getDisplayName() + ".Online.Min");
    				int Std = Frdb.getInt(players.getDisplayName() + ".Online.Std");
    				if(Min == 60) {
    					Min = 0;
    					Std = Std+1;
    					players.sendMessage("Du insgesammt : "+Std+" Stunden auf dem Server, dafür bekommst du einen Punkt");
    					Mysql.Punkte.Update(players.getUniqueId(), 1, players.getDisplayName(), false , players);
    				}else {
    					Min = Min+1;
    				}
    				Frdb.set(players.getDisplayName() + ".Online.Min", Min);
    				Frdb.set(players.getDisplayName() + ".Online.Std", Std);
    			}
    			}
    	}, 20*60, 20*60);
    		
    	
    	
    	}
	

	public void onDisable() {
    	MySQL.disconnect();
        for(Player on:Bukkit.getServer().getOnlinePlayers()){
            on.kickPlayer(ChatColor.RED + "Der Server wird Neugestartet \n"+ChatColor.BLUE+"bitte warte kurz und versuche dann dich wieder zu Verbinden\n "+ChatColor.GREEN+"Come and Play On ");
            Main.Frdb.set(on.getName()+".isOnline", false);
     
        }
       	try {
    				Main.Frdb.save(Main.Friends);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
}

    
    
}