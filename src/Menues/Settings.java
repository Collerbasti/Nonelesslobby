package Menues;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Main.Main;

public class Settings {
	public static void Spawn(Player p) {
		Inventory Settings = p.getServer().createInventory(null, 27,p.getName()+"§b Settings");
		
		if(Main.Frdb.getBoolean(p.getName()+".AllowFriendsTp")){
			
			ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
	    	ItemMeta FATPM =  FATP.getItemMeta(); 
	    	FATPM.setDisplayName("Teleportieren von Freunden zu Einem Erlaubt");
	    	FATP.setItemMeta(FATPM);
	    	Settings.setItem(2, FATP);
		}else {
			ItemStack FATP = new ItemStack(Material.RED_WOOL,1,(byte)14 );
		    ItemMeta FATPM =  FATP.getItemMeta(); 
		    FATPM.setDisplayName("Teleportieren von Freunden zu Einem Verboten");
		    FATP.setItemMeta(FATPM);
		    Settings.setItem(2, FATP);
			}
		if(Main.Frdb.getBoolean(p.getName()+".Gamemode")) {
			ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
	    	ItemMeta FATPM =  FATP.getItemMeta(); 
	    	FATPM.setDisplayName("Gamemode Creative");
	    	FATP.setItemMeta(FATPM);
	    	Settings.setItem(4, FATP);	
		}	
		else {
			if(p.hasPermission("Noneless.Admin.Gamemode")) {
				ItemStack FATP = new ItemStack(Material.BLUE_WOOL,1,(byte)14 );
			    ItemMeta FATPM =  FATP.getItemMeta(); 
			    FATPM.setDisplayName("Gamemode Adventure");
			    FATP.setItemMeta(FATPM);
			    Settings.setItem(4, FATP);	
			}
			else {
				ItemStack FATP = new ItemStack(Material.LIGHT_GRAY_WOOL,7,(byte)14 );
			    ItemMeta FATPM =  FATP.getItemMeta(); 
			    FATPM.setDisplayName("Gamemode Verboten");
			    FATP.setItemMeta(FATPM);
			    Settings.setItem(4, FATP);
			}
		}
		
		ItemStack Back = new ItemStack(Material.BARRIER);
    	ItemMeta BMeta =  Back.getItemMeta(); 
    	BMeta.setDisplayName("Zurück");
    	Back.setItemMeta(BMeta);
    	Settings.setItem(26,Back);
		
		p.openInventory(Settings);
	}
}
