package ItemBuilder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	
	
	public static ItemStack addLore(String name , Material mat, String lore , int amount) {
		ItemStack s = new ItemStack(mat , amount);
		ItemMeta m = s.getItemMeta();
		List<String> lore1= new ArrayList<>();
		lore1.add(lore);
		m.setLore(lore1);
		m.addEnchant(Enchantment.DURABILITY, 1, true);
		m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		s.setItemMeta(m);
		return s;
		
	}


	
	
}
