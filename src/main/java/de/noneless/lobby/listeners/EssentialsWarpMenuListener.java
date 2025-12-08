package de.noneless.lobby.listeners;

import de.noneless.lobby.Menues.EssentialsWarpMenu;
import de.noneless.lobby.Menues.Warps;
import de.noneless.lobby.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EssentialsWarpMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        if (!EssentialsWarpMenu.isEssentialsWarpInventory(view)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        NamespacedKey key = EssentialsWarpMenu.getDataKey();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String action = container.get(key, PersistentDataType.STRING);
        if (action == null) {
            return;
        }

        EssentialsWarpMenu.MenuHolder holder = null;
        if (view.getTopInventory().getHolder() instanceof EssentialsWarpMenu.MenuHolder menuHolder) {
            holder = menuHolder;
        }
        int page = holder != null ? holder.getPage() : 0;

        switch (action) {
            case "nav:previous" -> new EssentialsWarpMenu().open(player, Math.max(0, page - 1));
            case "nav:next" -> new EssentialsWarpMenu().open(player, page + 1);
            case "nav:back" -> new Warps().Spawn(player);
            default -> {
                if (action.startsWith("warp:")) {
                    String warpName = action.substring("warp:".length());
                    player.closeInventory();
                    if (warpName.isEmpty()) {
                        player.sendMessage("Warp konnte nicht gelesen werden.");
                    } else {
                        player.performCommand("essentials:warp " + warpName);
                    }
                }
            }
        }
    }
}
