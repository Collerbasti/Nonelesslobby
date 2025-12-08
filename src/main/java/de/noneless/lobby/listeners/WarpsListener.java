package de.noneless.lobby.listeners;

import de.noneless.lobby.Menues.Settings;
import de.noneless.lobby.Menues.Warps;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class WarpsListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!Warps.isWarpsInventory(event.getView())) {
            return;
        }

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        NamespacedKey warpKey = Warps.getWarpKey();
        String action = meta.getPersistentDataContainer().get(warpKey, PersistentDataType.STRING);
        if (action == null) {
            return;
        }

        handleAction(player, action);
    }

    private void handleAction(Player player, String action) {
        switch (action) {
            case Warps.ACTION_ESSENTIALS -> {
                player.closeInventory();
                new de.noneless.lobby.Menues.EssentialsWarpMenu().open(player, 0);
            }
            case Warps.ACTION_FRIENDS -> {
                player.closeInventory();
                player.performCommand("friend");
            }
            case Warps.ACTION_SETTINGS -> {
                player.closeInventory();
                try {
                    new Settings().Spawn(player);
                } catch (Exception ex) {
                    player.sendMessage(ChatColor.RED + "Fehler beim Oeffnen der Einstellungen!");
                }
            }
            case Warps.ACTION_FOOD -> {
                player.closeInventory();
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
                player.sendMessage(ChatColor.GREEN + "Du wurdest gesaettigt!");
            }
            case Warps.ACTION_SKYBLOCK -> {
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "Teleportiere zu SkyBlock...");
                player.sendMessage(ChatColor.GRAY + "SkyBlock-Teleportation wird implementiert!");
            }
            case Warps.ACTION_GAMES -> {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Oeffne Games-Menue...");
                player.sendMessage(ChatColor.GRAY + "Games-Menue wird implementiert!");
            }
            default -> {
                // keine Aktion
            }
        }
    }
}
