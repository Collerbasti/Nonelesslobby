package Menues;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Warps {
public static void Spawn(Player ep) {
	
	Inventory Menue = ep.getServer().createInventory(null, 27,ep.getName()+"§b Warps");

	ItemStack Spawn3 = new ItemStack(Material.ACACIA_WOOD);
	ItemMeta Meta3 = Spawn3.getItemMeta(); 
	Meta3.setDisplayName("AREA City");
	Spawn3.setItemMeta(Meta3);
	Menue.setItem(12,Spawn3);
	
	ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
	SMeta.setDisplayName("Freunde");
	SMeta.setOwningPlayer(ep);
	Skull.setItemMeta(SMeta);
	Skull.setDurability((short) 3);
	Menue.setItem(13,Skull);
	
	ItemStack Set = new ItemStack(Material.COMPASS);
	ItemMeta CMeta =  Set.getItemMeta(); 
	CMeta.setDisplayName("Einstellungen");
	Set.setItemMeta(CMeta);
	Menue.setItem(14,Set);
	
	ItemStack Meat = new ItemStack(Material.BAKED_POTATO);
	ItemMeta MMeta =  Meat.getItemMeta(); 
	MMeta.setDisplayName("Essen");
	Meat.setItemMeta(MMeta);
	Menue.setItem(20,Meat);
	
	if(ep.hasPermission("Noneless.Creative.World")){
		ItemStack Creative = new ItemStack(Material.REDSTONE);
    	ItemMeta CMETA =  Creative.getItemMeta(); 
    	CMETA.setDisplayName("Kreativ welt");
    	Creative.setItemMeta(CMETA);
    	Menue.setItem(21,Creative);
		
	}
	if(ep.hasPermission("Noneless.Admin.World")){
		ItemStack Creative = new ItemStack(Material.GLOWSTONE_DUST);
    	ItemMeta CMETA =  Creative.getItemMeta(); 
    	CMETA.setDisplayName("Admin AreaCity");
    	Creative.setItemMeta(CMETA);
    	Menue.setItem(22,Creative);
		
	}
	
	
	
	
	ep.openInventory(Menue);
	
	
	
}
}
