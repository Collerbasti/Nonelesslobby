package de.noneless.Menues;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.noneless.Main;

public class Games {
    private static final int INVENTORY_SIZE = 27;
    private static final String GAMES_TITLE = "§b Minispiele";
    private static final int DUELL_SLOT = 28;

    @SuppressWarnings("deprecation")
    public static void Spawn(Player p) {
        if (p == null) return;
        Inventory games = p.getServer().createInventory(null, INVENTORY_SIZE, p.getName() + GAMES_TITLE);
        List<String> miniGames = Main.MiGm.getStringList("Global.Minigames");
        int count = Math.min(Main.MiGm.getInt("Global.Count"), miniGames.size());
        for (int i = 0; i < count; i++) {
            String game = miniGames.get(i);
            int mat = Main.MiGm.getInt(game + ".Mat");
            ItemStack item;
            ItemMeta itemMeta;
            switch (mat) {
                case 1:
                    item = new ItemStack(Material.BLACK_BED, 1);
                    itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(game);
                    item.setItemMeta(itemMeta);
                    break;
                case 2:
                    item = new ItemStack(Material.ACACIA_WOOD, 1);
                    itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(game);
                    item.setItemMeta(itemMeta);
                    break;
                default:
                    item = new ItemStack(Material.RED_MUSHROOM, 1);
                    itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(game);
                    item.setItemMeta(itemMeta);
                    break;
            }
            games.setItem(i, item);
        }
        // Duell-Button
        ItemStack duell = new ItemStack(Material.GLASS_PANE, 1);
        ItemMeta duellMeta = duell.getItemMeta();
        duellMeta.setDisplayName("Duell");
        duell.setItemMeta(duellMeta);
        if (DUELL_SLOT < INVENTORY_SIZE) {
            games.setItem(DUELL_SLOT - 1, duell);
        }
        p.openInventory(games);
    }
}
