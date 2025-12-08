package de.noneless.Menues;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.noneless.Main;

public class Freunde {
    private static final int INVENTORY_SIZE = 27;
    private static final int BACK_SLOT = 26;
    private static final String BACK_NAME = "Zurück";
    private static final String FRIENDS_TITLE = "§b Freunde";
    private static final String OFFLINE_COLOR = "§4";

    @SuppressWarnings("deprecation")
    public static void Spawn(Player p) {
        if (p == null) return;
        List<String> friends = Main.Frdb.getStringList(p.getName() + ".Friends");
        int count = Main.Frdb.getInt(p.getName() + ".Count");
        Inventory friendsMenu = p.getServer().createInventory(null, INVENTORY_SIZE, p.getName() + FRIENDS_TITLE);

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(BACK_NAME);
        back.setItemMeta(backMeta);
        friendsMenu.setItem(BACK_SLOT, back);

        for (int i = 0; i < count && i < friends.size(); i++) {
            String friendName = friends.get(i);
            boolean isOnline = Main.Frdb.getBoolean(friendName + ".isOnline");
            ItemStack skull;
            SkullMeta skullMeta;
            if (isOnline) {
                skull = new ItemStack(Material.PLAYER_HEAD);
                skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setDisplayName(friendName);
                Player friendPlayer = Bukkit.getPlayer(friendName);
                if (friendPlayer != null) {
                    skullMeta.setOwningPlayer(friendPlayer);
                }
                skull.setItemMeta(skullMeta);
                friendsMenu.setItem(i, skull);
            } else {
                skull = new ItemStack(Material.SKELETON_SKULL, 1);
                skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setDisplayName(OFFLINE_COLOR + friendName);
                skull.setItemMeta(skullMeta);
                friendsMenu.setItem(i, skull);
            }
        }
        p.openInventory(friendsMenu);
    }
}
