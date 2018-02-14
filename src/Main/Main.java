package Main;

import org.bukkit.plugin.java.JavaPlugin;

import commands.CMDtest;

public class Main extends JavaPlugin{
	public void onEnable() {
		
    	this.getCommand("test").setExecutor(new CMDtest());
    	
    	System.out.println("Das Plugin wurde aktiviert!");	

}
}
