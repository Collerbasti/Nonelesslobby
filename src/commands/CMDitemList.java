package commands;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import Main.Main;

public class CMDitemList implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		ArrayList<String> ShopsPreise = new ArrayList<String>();
		Player p = (Player) sender;
		if(sender instanceof Player);
		if(cmd.getName().equals("itemList"));
		if(!p.hasPermission("Noneless.itemList")) {
			p.sendMessage("§cDu darfst diesen Befehl nicht Benutzen");
		 return true;
		 
	} else { 
		int Counter = Main.shp.getInt("Items.Count");
		Inventory Items = p.getServer().createInventory(null, 36,p.getName()+"§b ItemListe");
		ShopsPreise.addAll(Main.shp.getStringList("Items.List"));
		while(Counter > 0) {
		Counter = Counter- 1;
			String M = ShopsPreise.get(Counter);
	    	ItemStack Skull = new ItemStack(Material.getMaterial(M));
	    	ItemMeta SMeta =  Skull.getItemMeta(); 
	    	SMeta.setDisplayName(M+" "+Main.shp.getString(ShopsPreise.get(Counter)+".ShopsPreise"));
	    	Skull.setItemMeta(SMeta);
	    	Items.setItem(Counter,Skull);
		}
		p.openInventory(Items);
	}
		return false;
	}
}