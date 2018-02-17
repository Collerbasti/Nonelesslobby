package Main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

import commands.CMDsetlobby;
import commands.CMDspawn;
import commands.CMDtest;

public class Main extends JavaPlugin implements Listener{
	
	public static File Locations;
	public static FileConfiguration loc;
	public static File Friends;
	public static FileConfiguration Frdb;
	

	
	@Override	

	public void onEnable() {

		
	

		Bukkit.getPluginManager().registerEvents(this, this);

		new NonelessEventListener(this);
		
		this.getCommand("test").setExecutor(new CMDtest());
		this.getCommand("spawn").setExecutor(new CMDspawn());
		this.getCommand("setlobby").setExecutor(new CMDsetlobby());
		this.getCommand("addFriend").setExecutor(new CMDsetlobby());
    	
    	//Setupfiles Erzeugen
		
    	Main.Locations = new File("plugins/Noneless","Warps.yml");
    	Main.loc = YamlConfiguration.loadConfiguration(Main.Locations);
    	
    	
    	Main.Friends = new File("plugins/Noneless/Friends","Friends.yml");
    	Main.Frdb = YamlConfiguration.loadConfiguration(Main.Friends);  
    	Main.Frdb.set("Enabled", true);
    	
    	System.out.println("Das Plugin wurde aktiviert!");	


    	}

}
