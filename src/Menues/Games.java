package Menues;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Main.Main;

public class Games {
	public static void Spawn(Player p) {
		Inventory Games = p.getServer().createInventory(null, 27, p.getName()+"§b Minispiele");
		
		int Counter = Main.MiGm.getInt("Global.Count");
		ArrayList<String> MiniGames = new ArrayList<String>();
		MiniGames.addAll(Main.MiGm.getStringList("Global.Minigames"));
		
			while(Counter > 0 ){
				
				Counter = Counter - 1;
				String Game = MiniGames.get(Counter);
				
				if(Main.MiGm.getInt(MiniGames.get(Counter)+".Mat") == 1) {
					ItemStack FATP = new ItemStack(Material.BLACK_BED,1,(byte)14 );
					ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName(Game);
				    FATP.setItemMeta(FATPM);
				    Games.setItem(Counter, FATP);
				}else if(Main.MiGm.getInt(MiniGames.get(Counter)+".Mat") == 2) {
					ItemStack FATP = new ItemStack(Material.ACACIA_WOOD,1);
					ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName(Game);
				    FATP.setItemMeta(FATPM);
				    Games.setItem(Counter, FATP);
				}else {
					ItemStack FATP = new ItemStack(Material.RED_MUSHROOM,1,(byte)14 );
					ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName(Game);
				    FATP.setItemMeta(FATPM);
				    Games.setItem(Counter, FATP);
				}
				
				
			    
				
			}
			Counter = Main.MiGm.getInt("Global.Count")+1;
			ItemStack FATP = new ItemStack(Material.GLASS_PANE,1);
			ItemMeta FATPM =  FATP.getItemMeta(); 
		    FATPM.setDisplayName("Duell");
		    FATP.setItemMeta(FATPM);
		    Games.setItem(Counter, FATP);
			
			
			
		p.openInventory(Games);
	}
}
