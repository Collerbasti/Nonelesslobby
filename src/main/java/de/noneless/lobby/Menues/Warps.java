package de.noneless.lobby.Menues;

import de.noneless.lobby.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Warps {

    public static final String ACTION_ESSENTIALS = "essentials";
    public static final String ACTION_FRIENDS = "friends";
    public static final String ACTION_SETTINGS = "settings";
    public static final String ACTION_FOOD = "food";
    public static final String ACTION_SKYBLOCK = "skyblock";
    public static final String ACTION_GAMES = "games";

    private static NamespacedKey warpKey;

    public void Spawn(Player player) {
        Component title = Component.text(player.getName(), NamedTextColor.AQUA)
                .append(Component.text(" Noneless Lobby", NamedTextColor.GOLD));
        MenuHolder holder = new MenuHolder();
        Inventory menu = Bukkit.createInventory(holder, 27, title);
        holder.bind(menu);

        menu.setItem(11, createWarpItem(
                Material.ENDER_PEARL,
                Component.text("Essentials Warps", NamedTextColor.GOLD),
                List.of(Component.text("Alle Essentials-Warps anzeigen", NamedTextColor.GRAY)),
                ACTION_ESSENTIALS
        ));

        menu.setItem(12, createFriendsItem(player));

        menu.setItem(13, createWarpItem(
                Material.COMPASS,
                Component.text("Einstellungen", NamedTextColor.YELLOW),
                List.of(Component.text("Oeffne die Einstellungen", NamedTextColor.GRAY)),
                ACTION_SETTINGS
        ));

        menu.setItem(20, createWarpItem(
                Material.BAKED_POTATO,
                Component.text("Essen", NamedTextColor.GREEN),
                List.of(Component.text("Stille deinen Hunger", NamedTextColor.GRAY)),
                ACTION_FOOD
        ));

        menu.setItem(23, createWarpItem(
                Material.GOLDEN_PICKAXE,
                Component.text("SkyBlock", NamedTextColor.GOLD),
                List.of(Component.text("Besuche die SkyBlock Welt", NamedTextColor.GRAY)),
                ACTION_SKYBLOCK
        ));

        menu.setItem(24, createWarpItem(
                Material.BLACK_BANNER,
                Component.text("Games", NamedTextColor.RED),
                List.of(Component.text("Spiele Minigames", NamedTextColor.GRAY)),
                ACTION_GAMES
        ));

        fillWithGlass(menu);
        player.openInventory(menu);
    }

    public static boolean isWarpsInventory(InventoryView view) {
        if (view == null) {
            return false;
        }
        Inventory top = view.getTopInventory();
        return top != null && top.getHolder() instanceof MenuHolder;
    }

    public static NamespacedKey getWarpKey() {
        if (warpKey == null) {
            Main plugin = Main.getInstance();
            if (plugin == null) {
                throw new IllegalStateException("Warps menu requested before plugin initialisation.");
            }
            warpKey = new NamespacedKey(plugin, "warps-menu");
        }
        return warpKey;
    }

    private ItemStack createWarpItem(Material material, Component displayName, List<Component> lore, String actionId) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(displayName);
        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore);
        }
        if (actionId != null) {
            meta.getPersistentDataContainer().set(getWarpKey(), PersistentDataType.STRING, actionId);
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createFriendsItem(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            skullMeta.displayName(Component.text("Freunde", NamedTextColor.AQUA));
            skullMeta.lore(List.of(Component.text("Verwalte deine Freunde", NamedTextColor.GRAY)));
            skullMeta.getPersistentDataContainer().set(getWarpKey(), PersistentDataType.STRING, ACTION_FRIENDS);
            item.setItemMeta(skullMeta);
            return item;
        }
        return createWarpItem(
                Material.PLAYER_HEAD,
                Component.text("Freunde", NamedTextColor.AQUA),
                List.of(Component.text("Verwalte deine Freunde", NamedTextColor.GRAY)),
                ACTION_FRIENDS
        );
    }

    private void fillWithGlass(Inventory menu) {
        ItemStack filler = createWarpItem(
                Material.BLACK_STAINED_GLASS_PANE,
                Component.text(" ", NamedTextColor.DARK_GRAY),
                null,
                null
        );
        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, filler.clone());
            }
        }
    }

    private static final class MenuHolder implements InventoryHolder {
        private Inventory inventory;

        void bind(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
