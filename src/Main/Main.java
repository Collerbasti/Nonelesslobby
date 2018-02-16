package Main;

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

@Override	
	public void onEnable() {
		
	
		new NonelessEventListener(this);
		
		this.getCommand("test").setExecutor(new CMDtest());
		this.getCommand("spawn").setExecutor(new CMDspawn());
		this.getCommand("setlobby").setExecutor(new CMDsetlobby());
    	
    	//Locationfile Erzeugen
		
    	Main.Locations = new File("plugins/Noneless","locations.yml");
    	Main.loc = YamlConfiguration.loadConfiguration(Main.Locations);
    	
    	System.out.println("Das Plugin wurde aktiviert!");	


    	}
}
