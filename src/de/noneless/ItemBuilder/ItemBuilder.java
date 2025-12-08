package de.noneless.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
    public static ItemStack addLore(String name, Material mat, String lore, int amount) {
        if (mat == null || lore == null || amount <= 0) return null;
        ItemStack s = new ItemStack(mat, amount);
        ItemMeta m = s.getItemMeta();
        if (m == null) return s;
        List<String> loreList = new ArrayList<>();
        loreList.add(lore);
        m.setLore(loreList);
        if (name != null && !name.isEmpty()) {
            m.setDisplayName(name);
        }
        m.addEnchant(Enchantment.DURABILITY, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        s.setItemMeta(m);
        return s;
    }
}
