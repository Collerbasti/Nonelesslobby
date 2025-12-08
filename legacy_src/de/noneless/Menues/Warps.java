package de.noneless.Menues;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Warps {
    private static final int INVENTORY_SIZE = 27;
    private static final String MENU_TITLE = "§b Noneless Lobby";
    private static final int SPAWN_SLOT = 12;
    private static final int FREUNDE_SLOT = 13;
    private static final int SETTINGS_SLOT = 14;
    private static final int ESSEN_SLOT = 21;
    private static final int SKYBLOCK_SLOT = 24;
    private static final int GAMES_SLOT = 25;
    private static final int CREATIVE_SLOT = 22;
    private static final int ADMIN_SLOT = 23;

    @SuppressWarnings("deprecation")
    public static void Spawn(Player ep) {
        if (ep == null) return;
        Inventory menue = ep.getServer().createInventory(null, INVENTORY_SIZE, ep.getName() + MENU_TITLE);
        // Spawn
        ItemStack spawn = new ItemStack(Material.ACACIA_WOOD);
        ItemMeta spawnMeta = spawn.getItemMeta();
        spawnMeta.setDisplayName("AREA City");
        spawn.setItemMeta(spawnMeta);
        menue.setItem(SPAWN_SLOT, spawn);
        // Freunde
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName("Freunde");
        skullMeta.setOwningPlayer(ep);
        skull.setItemMeta(skullMeta);
        menue.setItem(FREUNDE_SLOT, skull);
        // Einstellungen
        ItemStack set = new ItemStack(Material.COMPASS);
        ItemMeta setMeta = set.getItemMeta();
        setMeta.setDisplayName("Einstellungen");
        set.setItemMeta(setMeta);
        menue.setItem(SETTINGS_SLOT, set);
        // Essen
        ItemStack meat = new ItemStack(Material.BAKED_POTATO);
        ItemMeta meatMeta = meat.getItemMeta();
        meatMeta.setDisplayName("Essen");
        meat.setItemMeta(meatMeta);
        menue.setItem(ESSEN_SLOT, meat);
        // BauLobby-Warp-Button (ersetzt Kreativ/Admin/Skyblock/Games)
        ItemStack baulobby = new ItemStack(Material.BRICKS);
        ItemMeta baulobbyMeta = baulobby.getItemMeta();
        baulobbyMeta.setDisplayName("BauLobby");
        baulobby.setItemMeta(baulobbyMeta);
        menue.setItem(22, baulobby); // Slot 22 für BauLobby
        // Warps-Menü
        ItemStack warps = new ItemStack(Material.ENDER_PEARL);
        ItemMeta warpsMeta = warps.getItemMeta();
        warpsMeta.setDisplayName("Warps");
        warps.setItemMeta(warpsMeta);
        menue.setItem(10, warps); // Slot 10 für Warps
        // Homes-Menü
        ItemStack homes = new ItemStack(Material.RED_BED);
        ItemMeta homesMeta = homes.getItemMeta();
        homesMeta.setDisplayName("Homes");
        homes.setItemMeta(homesMeta);
        menue.setItem(16, homes); // Slot 16 für Homes
        // Leere Felder
        ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta emptyMeta = empty.getItemMeta();
        emptyMeta.setDisplayName(" ");
        empty.setItemMeta(emptyMeta);
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (menue.getItem(i) == null) {
                menue.setItem(i, empty);
            }
        }
        ep.openInventory(menue);
    }
}
