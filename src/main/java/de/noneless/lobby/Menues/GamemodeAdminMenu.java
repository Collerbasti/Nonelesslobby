package de.noneless.lobby.Menues;

import Config.GamemodeSettingsConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GamemodeAdminMenu {

    public static final String TITLE = ChatColor.DARK_AQUA + "Gamemode Verwaltung";

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // World defaults button
        ItemStack worldItem = new ItemStack(Material.FILLED_MAP);
        ItemMeta worldMeta = worldItem.getItemMeta();
        worldMeta.setDisplayName(ChatColor.GOLD + "Welt-Gamemodes");
        List<String> worldLore = new ArrayList<>();
        worldLore.add(ChatColor.GRAY + "Lege den Standard-Gamemode");
        worldLore.add(ChatColor.GRAY + "für jede Welt fest.");
        worldLore.add(ChatColor.YELLOW + "Klicke, um Welten zu verwalten.");
        worldMeta.setLore(worldLore);
        worldItem.setItemMeta(worldMeta);
        inv.setItem(11, worldItem);

        // Creative override button
        ItemStack overrideItem = buildOverrideItem(player);
        inv.setItem(15, overrideItem);

        // Back button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "Zurück");
        back.setItemMeta(backMeta);
        inv.setItem(22, back);

        player.openInventory(inv);
    }

    private ItemStack buildOverrideItem(Player player) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Creative Override");
        List<String> lore = new ArrayList<>();
        long expiresAt = GamemodeSettingsConfig.getCreativeOverrideExpiry(player.getUniqueId());
        if (expiresAt > System.currentTimeMillis()) {
            long remaining = expiresAt - System.currentTimeMillis();
            lore.add(ChatColor.GREEN + "Aktiv");
            lore.add(ChatColor.GRAY + "Restzeit: " + ChatColor.YELLOW + formatDuration(remaining));
            lore.add(ChatColor.YELLOW + "Klicke, um zu deaktivieren.");
        } else {
            lore.add(ChatColor.RED + "Inaktiv");
            lore.add(ChatColor.GRAY + "Gewährt 7 Stunden Creative");
            lore.add(ChatColor.YELLOW + "Klicke, um zu aktivieren.");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static String formatDuration(long millis) {
        if (millis <= 0) {
            return "0m";
        }
        Duration duration = Duration.ofMillis(millis);
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();
        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            builder.append(minutes).append("m ");
        }
        builder.append(seconds).append("s");
        return builder.toString().trim();
    }
}
