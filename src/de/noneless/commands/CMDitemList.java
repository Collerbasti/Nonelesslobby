package de.noneless.commands;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.noneless.Main;

public class CMDitemList implements CommandExecutor  
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("Noneless.itemList")) {
            p.sendMessage("§cDu darfst diesen Befehl nicht benutzen");
            return true;
        }
        int counter = de.noneless.Main.shp.getInt("Items.Count");
        ArrayList<String> shopsPreise = new ArrayList<>(de.noneless.Main.shp.getStringList("Items.List"));
        Inventory items = p.getServer().createInventory(null, 36, p.getName() + "§b ItemListe");
        for (int i = 0; i < counter && i < shopsPreise.size(); i++) {
            String m = shopsPreise.get(i);
            ItemStack skull = new ItemStack(Material.getMaterial(m));
            ItemMeta sMeta = skull.getItemMeta();
            sMeta.setDisplayName(m + " " + de.noneless.Main.shp.getString(m + ".ShopsPreise"));
            skull.setItemMeta(sMeta);
            items.setItem(i, skull);
        }
        p.openInventory(items);
        return true;
	}
}
