package de.noneless.Menues;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class WarpsMenu {
    public static void open(Player player, List<String> warps) {
        int size = ((warps.size() - 1) / 9 + 1) * 9;
        Inventory inv = Bukkit.createInventory(null, Math.max(9, Math.min(size, 54)), "§b Warps");
        for (int i = 0; i < warps.size() && i < 54; i++) {
            ItemStack item = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(warps.get(i));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }
}
