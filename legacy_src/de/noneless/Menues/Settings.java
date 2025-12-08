package de.noneless.Menues;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.noneless.Main;

public class Settings {
    private static final int INVENTORY_SIZE = 27;
    private static final int FRIENDS_TP_SLOT = 2;
    private static final int GAMEMODE_SLOT = 4;
    private static final int BACK_SLOT = 26;
    private static final String SETTINGS_TITLE = "§b Settings";
    private static final String BACK_NAME = "Zurück";

    public static void Spawn(Player p) {
        if (p == null) return;
        Inventory settings = p.getServer().createInventory(null, INVENTORY_SIZE, p.getName() + SETTINGS_TITLE);
        // Freunde-Teleport
        boolean allowFriendsTp = Main.Frdb.getBoolean(p.getName() + ".AllowFriendsTp");
        ItemStack fatp = new ItemStack(allowFriendsTp ? Material.GREEN_WOOL : Material.RED_WOOL, 1);
        ItemMeta fatpMeta = fatp.getItemMeta();
        fatpMeta.setDisplayName(allowFriendsTp ? "Teleportieren von Freunden zu Einem Erlaubt" : "Teleportieren von Freunden zu Einem Verboten");
        fatp.setItemMeta(fatpMeta);
        settings.setItem(FRIENDS_TP_SLOT, fatp);
        // Gamemode
        boolean gamemode = Main.Frdb.getBoolean(p.getName() + ".Gamemode");
        if (gamemode) {
            ItemStack gm = new ItemStack(Material.GREEN_WOOL, 1);
            ItemMeta gmMeta = gm.getItemMeta();
            gmMeta.setDisplayName("Gamemode Creative");
            gm.setItemMeta(gmMeta);
            settings.setItem(GAMEMODE_SLOT, gm);
        } else if (p.hasPermission("Noneless.Admin.Gamemode")) {
            ItemStack gm = new ItemStack(Material.BLUE_WOOL, 1);
            ItemMeta gmMeta = gm.getItemMeta();
            gmMeta.setDisplayName("Gamemode Adventure");
            gm.setItemMeta(gmMeta);
            settings.setItem(GAMEMODE_SLOT, gm);
        } else {
            ItemStack gm = new ItemStack(Material.LIGHT_GRAY_WOOL, 1);
            ItemMeta gmMeta = gm.getItemMeta();
            gmMeta.setDisplayName("Gamemode Verboten");
            gm.setItemMeta(gmMeta);
            settings.setItem(GAMEMODE_SLOT, gm);
        }
        // Zurück-Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(BACK_NAME);
        back.setItemMeta(backMeta);
        settings.setItem(BACK_SLOT, back);
        p.openInventory(settings);
    }
}
