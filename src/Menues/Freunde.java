package Menues;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import Main.Main;

public class Freunde {
	@SuppressWarnings("deprecation")
	public static void Spawn(Player p) {


		ArrayList<String> Friends = new ArrayList<String>();
		int Counter = Main.Frdb.getInt(p.getName()+".Count");
		Friends.addAll(Main.Frdb.getStringList(p.getName()+".Friends"));
		
		Inventory FriendsMenue = p.getServer().createInventory(null, 27,p.getName()+"§b Freunde");
		
    	ItemStack Back = new ItemStack(Material.BARRIER);
    	ItemMeta BMeta =  Back.getItemMeta(); 
    	BMeta.setDisplayName("Zurück");
    	Back.setItemMeta(BMeta);
    	FriendsMenue.setItem(26,Back);
		
	while(Counter > 0) {	
		Counter = Counter -1;
		if(Main.Frdb.getBoolean(Friends.get(Counter).toString()+".isOnline")){
		
		
    	
    	ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
    	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
    	SMeta.setDisplayName(Friends.get(Counter));
		Player Fp = Bukkit.getPlayer(Friends.get(Counter));
    	SMeta.setOwningPlayer(Fp);
    	Skull.setItemMeta(SMeta);
    	Skull.setDurability((short) 3);
    	FriendsMenue.setItem(Counter,Skull);
    	
    	

    	
    	
		
		
		}else {
			
	    	ItemStack Skull = new ItemStack(Material.SKELETON_SKULL , 1);
	    	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
	    	SMeta.setDisplayName("§4"+Friends.get(Counter));
	    	Skull.setItemMeta(SMeta);
	    	
	    	FriendsMenue.setItem(Counter,Skull);
			
		}
		
		p.openInventory(FriendsMenue);

		
		}
	}
}
