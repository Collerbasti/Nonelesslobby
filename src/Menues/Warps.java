package Menues;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Warps {
@SuppressWarnings("deprecation")
public static void Spawn(Player ep) {
	
	Inventory Menue = ep.getServer().createInventory(null, 27,ep.getName()+"§b Noneless Lobby");
	ArrayList<String> Items = new ArrayList<String>();
	ItemStack Spawn3 = new ItemStack(Material.ACACIA_WOOD);
	ItemMeta Meta3 = Spawn3.getItemMeta(); 
	Meta3.setDisplayName("AREA City");
	Spawn3.setItemMeta(Meta3);
	Items.add("12");
	Menue.setItem(12,Spawn3);
	
	ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
	SMeta.setDisplayName("Freunde");
	SMeta.setOwningPlayer(ep);
	Skull.setItemMeta(SMeta);
	Skull.setDurability((short) 3);
	Items.add("13");
	Menue.setItem(13,Skull);
	
	ItemStack Set = new ItemStack(Material.COMPASS);
	ItemMeta CMeta =  Set.getItemMeta(); 
	CMeta.setDisplayName("Einstellungen");
	Set.setItemMeta(CMeta);
	Items.add("14");
	Menue.setItem(14,Set);
	
	ItemStack Meat = new ItemStack(Material.BAKED_POTATO);
	ItemMeta MMeta =  Meat.getItemMeta(); 
	MMeta.setDisplayName("Essen");
	Meat.setItemMeta(MMeta);
	Items.add("21");
	Menue.setItem(21,Meat);
	
	ItemStack Cmd = new ItemStack(Material.GOLDEN_PICKAXE);
	ItemMeta MCmd =  Cmd.getItemMeta(); 
	MCmd.setDisplayName("SkyBlock");
	Cmd.setItemMeta(MCmd);
	Items.add("24");
	Menue.setItem(24,Cmd);
	
	ItemStack Games = new ItemStack(Material.GOLDEN_PICKAXE);
	ItemMeta MGames =  Games.getItemMeta(); 
	MGames.setDisplayName("Games");
	Games.setItemMeta(MGames);
	Items.add("25");
	Menue.setItem(25,Games);
	
	if(ep.hasPermission("Noneless.Creative.World")){
		ItemStack Creative = new ItemStack(Material.REDSTONE);
    	ItemMeta CMETA =  Creative.getItemMeta(); 
    	CMETA.setDisplayName("Kreativ welt");
    	Creative.setItemMeta(CMETA);
    	Items.add("22");
    	Menue.setItem(22,Creative);
		
	}
	if(ep.hasPermission("Noneless.Admin.World")){
		ItemStack Creative = new ItemStack(Material.GLOWSTONE_DUST);
    	ItemMeta CMETA =  Creative.getItemMeta(); 
    	CMETA.setDisplayName("Admin AreaCity");
    	Creative.setItemMeta(CMETA);
    	Items.add("23");
    	Menue.setItem(23,Creative);
		
	}
	ItemStack Emty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
	ItemMeta EMeta = Emty.getItemMeta();
	EMeta.setDisplayName(" ");
	Emty.setItemMeta(EMeta);
	
	
	int count = 0;
	while(count!=27) {
		if(!Items.contains(Integer.toString(count))) {
			Menue.setItem(count, Emty);
		}
		count =count+1;
	}
	
	
	ep.openInventory(Menue);
	
	
	
}
}
