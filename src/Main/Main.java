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
import commands.CMDaddItem;
import commands.CMDdelFriend;
import commands.CMDitemList;
import commands.CMDreport;
import commands.CMDsetlobby;
import commands.CMDspawn;
import commands.CMDvote;

public class Main extends JavaPlugin implements Listener{
	
	public static File Locations;
	public static FileConfiguration loc;
	public static File Friends;
	public static FileConfiguration Frdb;
	public static File ShopsPreise;
	public static FileConfiguration shp; 
	

	
	@Override	

	public void onEnable() {

		
	

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
    	
    	//Setupfiles Erzeugen
		
    	Main.Locations = new File("plugins/Noneless","Warps.yml");
    	Main.loc = YamlConfiguration.loadConfiguration(Main.Locations);
    	
    	Main.Friends = new File("plugins/Noneless","FriendsDB.yml");
    	Main.Frdb = YamlConfiguration.loadConfiguration(Main.Friends); 
    	
    	
    	Main.ShopsPreise = new File("plugins/Noneless","ShopItems.yml");
    	Main.shp = YamlConfiguration.loadConfiguration(Main.ShopsPreise);
    	
    	
    	System.out.println("Das Plugin wurde aktiviert!");	


    	}
	
    public void onDisable() {
        for(Player on:Bukkit.getServer().getOnlinePlayers()){
            on.kickPlayer(ChatColor.RED + "Der Server wird Neugestartet \n"+ChatColor.BLUE+"bitte warte kurtz und versuche dann dich wieder zu Verbinden\n "+ChatColor.GREEN+"Come and Play On ");
            Main.Frdb.set(on.getName()+".isOnline", false);
     
        }
       	try {
    				Main.Frdb.save(Main.Friends);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
}
    public void onReload() {
    	
    }
}